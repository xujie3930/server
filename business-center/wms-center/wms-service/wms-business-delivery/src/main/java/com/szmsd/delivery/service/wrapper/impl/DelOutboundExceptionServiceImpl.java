package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.dto.DelOutboundAgainTrackingNoDto;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.Utils;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.returnex.api.feign.serivice.IReturnExpressFeignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DelOutboundExceptionServiceImpl implements IDelOutboundExceptionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private IReturnExpressFeignService returnExpressFeignService;

    @Transactional
    @Override
    public boolean againTrackingNo(DelOutbound delOutbound, DelOutboundAgainTrackingNoDto dto) {
        String orderNo = delOutbound.getOrderNo();
        // 删除原来的地址信息，重新新增一个地址信息
        LambdaQueryWrapper<DelOutboundAddress> addressLambdaQueryWrapper = Wrappers.lambdaQuery();
        addressLambdaQueryWrapper.eq(DelOutboundAddress::getOrderNo, orderNo);
        this.delOutboundAddressService.remove(addressLambdaQueryWrapper);
        DelOutboundAddress delOutboundAddress = BeanMapperUtil.map(dto.getAddress(), DelOutboundAddress.class);
        if (Objects.nonNull(delOutboundAddress)) {
            delOutboundAddress.setOrderNo(orderNo);
            this.delOutboundAddressService.save(delOutboundAddress);
        }
        // 根据产品编码查询产品信息
        String productCode = dto.getShipmentRule();
        PricedProductInfo pricedProductInfo = htpPricedProductClientService.infoAndSubProducts(productCode);
        if (null == pricedProductInfo) {
            // 异常信息
            throw new CommonException("400", "查询产品[" + productCode + "]信息失败");
        }

        DelOutboundWrapperContext delOutboundWrapperContext = this.delOutboundBringVerifyService.initContext(delOutbound);

        // 更新物流服务信息
        DelOutbound wrapperContextDelOutbound = delOutboundWrapperContext.getDelOutbound();
        wrapperContextDelOutbound.setShipmentRule(productCode);
        wrapperContextDelOutbound.setShipmentService(pricedProductInfo.getLogisticsRouteId());

        // PRC计算费用校验
        ResponseObject<ChargeWrapper, ProblemDetails> prcResponseObject = delOutboundBringVerifyService.pricing(delOutboundWrapperContext, PricingEnum.PACKAGE);
        if (null == prcResponseObject) {
            // 返回值是空的
            throw new CommonException("400", "计算包裹费用失败[重新获取挂号]");
        } else {
            // 判断返回值
            if (!prcResponseObject.isSuccess()) {
                // 计算失败
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(prcResponseObject.getError()), "计算包裹费用失败2[重新获取挂号]");
                throw new CommonException("400", exceptionMessage);
            }
        }

        /*
        // 创建承运商物流订单
        ShipmentOrderResult shipmentOrderResult = delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);

        // 更新WMS挂号
        ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
        shipmentTrackingChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
        shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
        shipmentTrackingChangeRequestDto.setTrackingNo(shipmentOrderResult.getMainTrackingNumber());
        htpOutboundClientService.shipmentTracking(shipmentTrackingChangeRequestDto);

        // 获取标签
        CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
        command.setWarehouseCode(delOutbound.getWarehouseCode());
        command.setOrderNumber(shipmentOrderResult.getOrderNumber());
        ResponseObject<FileStream, ProblemDetails> responseObject = htpCarrierClientService.label(command);
        File labelFile = null;
        if (null != responseObject) {
            if (responseObject.isSuccess()) {
                FileStream fileStream = responseObject.getObject();
                String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound);
                File file = new File(pathname);
                if (!file.exists()) {
                    try {
                        FileUtils.forceMkdir(file);
                    } catch (IOException e) {
                        throw new CommonException("500", "创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage());
                    }
                }
                byte[] inputStream;
                if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                    labelFile = new File(file.getPath() + "/" + shipmentOrderResult.getOrderNumber());
                    try {
                        FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                    } catch (IOException e) {
                        throw new CommonException("500", "保存标签文件失败，Error：" + e.getMessage());
                    }
                }
            } else {
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "获取标签文件流失败2");
                throw new CommonException("500", exceptionMessage);
            }
        } else {
            throw new CommonException("500", "获取标签文件流失败");
        }

        if (null == labelFile) {
            throw new CommonException("500", "保存标签文件失败");
        }
        // 更新标签
        if (!labelFile.exists()) {
            throw new CommonException("500", "标签文件不存在");
        }
        try {
            byte[] byteArray = FileUtils.readFileToByteArray(labelFile);
            String encode = Base64.encode(byteArray);
            ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
            shipmentLabelChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
            shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
            shipmentLabelChangeRequestDto.setLabel(encode);
            IHtpOutboundClientService htpOutboundClientService = SpringUtils.getBean(IHtpOutboundClientService.class);
            ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
            if (null == responseVO || null == responseVO.getSuccess()) {
                throw new CommonException("400", "更新标签失败");
            }
            if (!responseVO.getSuccess()) {
                throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "更新标签失败2"));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "读取标签文件失败");
        }
        */

        LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        // lambdaUpdateWrapper.set(DelOutbound::getTrackingNo, shipmentOrderResult.getMainTrackingNumber());
        // lambdaUpdateWrapper.set(DelOutbound::getShipmentOrderNumber, shipmentOrderResult.getOrderNumber());
        lambdaUpdateWrapper.set(DelOutbound::getShipmentRule, productCode);
        lambdaUpdateWrapper.set(DelOutbound::getShipmentService, pricedProductInfo.getLogisticsRouteId());
        lambdaUpdateWrapper.set(DelOutbound::getTrackingAcquireType, DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode());
        // 将状态设置成begin，重新执行核重之后的流程。
        lambdaUpdateWrapper.set(DelOutbound::getShipmentState, ShipmentEnum.BEGIN.name());
        lambdaUpdateWrapper.eq(DelOutbound::getId, delOutbound.getId());
        boolean update = delOutboundService.update(null, lambdaUpdateWrapper);
        Object[] params = new Object[]{delOutbound, productCode};
        DelOutboundOperationLogEnum.AGAIN_TRACKING_NO.listener(params);
        return update;
    }

    @Async
    @Override
    public void updateTrackNoByOutBoundNo(String orderNo) {
        DelOutbound delOutbound = this.delOutboundService.getByOrderNo(orderNo);
        // 判断是否为重派件
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            this.returnExpressFeignService.updateTrackNoByOutBoundNo(orderNo, delOutbound.getTrackingNo());
        }
    }
}
