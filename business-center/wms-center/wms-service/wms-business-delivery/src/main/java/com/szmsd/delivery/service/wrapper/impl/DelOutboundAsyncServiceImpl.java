package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.dto.CusFreezeBalanceDTO;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:29
 */
@Service
public class DelOutboundAsyncServiceImpl implements IDelOutboundAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private RechargesFeignService rechargesFeignService;

    @Transactional
    @Override
    public int shipmentPacking(Long id) {
        // 获取新的出库单信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        DelOutbound updateDelOutbound = new DelOutbound();
        updateDelOutbound.setId(id);
        try {
            DelOutboundWrapperContext delOutboundWrapperContext = this.delOutboundBringVerifyService.initContext(delOutbound);
            // 判断获取承运商信息
            if (DelOutboundTrackingAcquireTypeEnum.WAREHOUSE_SUPPLIER.getCode().equals(delOutbound.getTrackingAcquireType())) {
                String trackingNo = this.delOutboundBringVerifyService.shipmentOrder(delOutboundWrapperContext);
                // 保存挂号
                updateDelOutbound.setTrackingNo(trackingNo);
                // 更新WMS挂号
                ShipmentTrackingChangeRequestDto shipmentTrackingChangeRequestDto = new ShipmentTrackingChangeRequestDto();
                shipmentTrackingChangeRequestDto.setOrderNo(delOutbound.getRefOrderNo());
                shipmentTrackingChangeRequestDto.setTrackingNo(trackingNo);
                this.htpOutboundClientService.shipmentTracking(shipmentTrackingChangeRequestDto);
            }
            // 获取运费信息
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = this.delOutboundBringVerifyService.pricing(delOutboundWrapperContext);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    Weight calcWeight = packageInfo.getCalcWeight();
                    updateDelOutbound.setCalcWeight(calcWeight.getValue());
                    updateDelOutbound.setCalcWeightUnit(calcWeight.getUnit());
                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    ChargeItem chargeItem = charges.get(0);
                    Money money = chargeItem.getMoney();
                    BigDecimal amount = Utils.valueOf(money.getAmount());
                    // 取消冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO.setAmount(delOutbound.getAmount());
                    cusFreezeBalanceDTO.setCurrencyCode(delOutbound.getCurrencyCode());
                    cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                    R<?> thawBalanceR = this.rechargesFeignService.thawBalance(cusFreezeBalanceDTO);
                    if (null == thawBalanceR || Constants.SUCCESS != thawBalanceR.getCode()) {
                        throw new CommonException("999", "取消冻结费用失败");
                    }
                    // 冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO2 = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO2.setAmount(amount);
                    cusFreezeBalanceDTO2.setCurrencyCode(money.getCurrencyCode());
                    cusFreezeBalanceDTO2.setCusCode(delOutbound.getSellerCode());
                    R<?> freezeBalanceR = this.rechargesFeignService.freezeBalance(cusFreezeBalanceDTO2);
                    ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                    shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                    shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                    shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                    shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                    if (null != freezeBalanceR && Constants.SUCCESS == freezeBalanceR.getCode()) {
                        // 更新发货指令
                        shipmentUpdateRequestDto.setIsEx(false);
                        shipmentUpdateRequestDto.setExType(null);
                        shipmentUpdateRequestDto.setExRemark(null);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    } else {
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("FreezeBalanceError");
                        shipmentUpdateRequestDto.setExRemark("冻结费用信息失败");
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                        // 异常信息
                        throw new CommonException("999", "冻结费用信息失败");
                    }
                    // 更新费用信息
                    updateDelOutbound.setAmount(amount);
                    updateDelOutbound.setCurrencyCode(money.getCurrencyCode());
                    this.delOutboundService.updateById(updateDelOutbound);
                }
            }
        } catch (CommonException e) {
            logger.error(e.getMessage(), e);
            String exceptionMessage = e.getMessage();
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            this.delOutboundService.bringVerifyFail(id, exceptionMessage);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 提审失败
            String exceptionMessage = "提审操作失败";
            this.delOutboundService.bringVerifyFail(id, exceptionMessage);
            throw new CommonException("999", exceptionMessage);
        }
        return 1;
    }
}
