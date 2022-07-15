package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelTrack;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelTrackService;
import com.szmsd.delivery.service.wrapper.IDelOutboundOpenService;
import com.szmsd.delivery.util.Utils;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentUpdateRequestDto;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 14:53
 */
@Service
public class DelOutboundOpenServiceImpl implements IDelOutboundOpenService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundOpenServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private RemoteAttachmentService attachmentService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelTrackService delTrackService;

    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("400", "单据不存在");
            }
            // 更新包裹信息
            int result;
            boolean isPackingMaterial = dto.isPackingMaterial();
            if (isPackingMaterial) {
                result = this.delOutboundService.shipmentPackingMaterial(dto);
            } else {
                String orderType = delOutbound.getOrderType();
                boolean overBreak = false;
                // 验证重量有没有超出返回
                if ((DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType) || DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType))
                        // 需要确认重量信息
                        && "076002".equals(delOutbound.getPackageConfirm())) {
                    // 仓库重量
                    double weight = Utils.defaultValue(dto.getWeight());
                    // 录单填写的重量
                    double weight1 = Utils.defaultValue(delOutbound.getWeight());
                    long deviation = Utils.defaultValue(delOutbound.getPackageWeightDeviation());
                    // 判断有没有超过误差重量
                    double abs = Math.abs(weight - weight1);
                    if (abs > deviation) {
                        // 超重，不再继续处理
                        overBreak = true;
                        // 发送超重的发货指令
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String templateFormat = "仓库称重{0}g，客户下单重量{1}g，差异值{2}g，超过误差范围{3}g，已拦截，请尽快提供处理意见。";
                        String exRemark = MessageFormat.format(templateFormat, weight, weight1, abs, deviation);
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                } else if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                    // 判断是否需要上传箱标
                    // 批量出库，判断有没有上传箱标
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isEmpty(attachmentList)) {
                            // 箱标文件不存在，不再继续处理
                            overBreak = true;
                            // 发送箱标文件未上传的发货指令
                            ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                            shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                            shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                            shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                            shipmentUpdateRequestDto.setIsEx(true);
                            shipmentUpdateRequestDto.setExType("OutboundIntercept");
                            String exRemark = "发货指令异常，箱标文件未上传";
                            shipmentUpdateRequestDto.setExRemark(exRemark);
                            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                            this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                        }
                    }
                }
                // 更新包裹信息
                result = this.delOutboundService.shipmentPacking(dto, orderType);
                // 处理结果大于0才认定为执行成功
                if (result > 0 && !overBreak) {
                    // 执行异步任务
                    // EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
                    // 增加出库单已取消记录，异步处理，定时任务
                    this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
                }
            }
            DelOutboundOperationLogEnum.OPN_PACKING.listener(new Object[]{delOutbound, isPackingMaterial});
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int shipmentContainers(ShipmentContainersRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("400", "单据不存在");
            }
            if (logger.isInfoEnabled()) {
                logger.info("======出库单据信息：{}", delOutbound);
            }
            boolean overBreak = false;
            String orderType = delOutbound.getOrderType();
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                if (logger.isInfoEnabled()) {
                    logger.info("======批量出库，需要打印标签，查询附件信息");
                }
                // 判断是否需要上传箱标
                // 批量出库，判断有没有上传箱标
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                if (logger.isInfoEnabled()) {
                    logger.info("======查询附件信息：{}", listR);
                }
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isEmpty(attachmentList)) {
                        // 箱标文件不存在，不再继续处理
                        overBreak = true;
                        // 发送箱标文件未上传的发货指令
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
                        } else {
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                        }
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String exRemark = "发货指令异常，箱标文件未上传";
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                }
            }
            DelOutboundOperationLogEnum.OPN_CONTAINERS.listener(delOutbound);
            // 更新包裹信息
            this.delOutboundService.shipmentContainers(dto);
            // 执行异步任务
            if (!overBreak) {
                // EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
                // 增加出库单已取消记录，异步处理，定时任务
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            }
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public int shipmentPackingMaterial(ShipmentPackingMaterialRequestDto dto) {
        return this.delOutboundService.shipmentPackingMaterialIgnoreState(dto);
    }

    @Override
    public int shipmentPackingMeasure(ShipmentPackingMaterialRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("400", "单据不存在");
            }
            if (logger.isInfoEnabled()) {
                logger.info("======出库单据信息：{}", delOutbound);
            }
            boolean overBreak = false;
            String orderType = delOutbound.getOrderType();
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                if (logger.isInfoEnabled()) {
                    logger.info("======批量出库，需要打印标签，查询附件信息");
                }
                // 判断是否需要上传箱标
                // 批量出库，判断有没有上传箱标
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                if (logger.isInfoEnabled()) {
                    logger.info("======查询附件信息：{}", listR);
                }
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isEmpty(attachmentList)) {
                        // 箱标文件不存在，不再继续处理
                        overBreak = true;
                        // 发送箱标文件未上传的发货指令
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
                        } else {
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                        }
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String exRemark = "发货指令异常，箱标文件未上传";
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                }
            }
            DelOutboundOperationLogEnum.OPN_CONTAINERS.listener(delOutbound);
            // 更新包裹信息
            this.delOutboundService.shipmentPacking(dto, null);
            // 执行异步任务
            if (!overBreak) {
                // 增加出库单已取消记录，异步处理，定时任务
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            }

            delTrackService.addData(new DelTrack()
                    .setOrderNo(delOutbound.getOrderNo())
                    .setTrackingNo(delOutbound.getTrackingNo())
                    .setTrackingStatus("InTransit")
                    .setDescription("DMF, Parcel is being processed at the "+delOutbound.getWarehouseCode()));
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
