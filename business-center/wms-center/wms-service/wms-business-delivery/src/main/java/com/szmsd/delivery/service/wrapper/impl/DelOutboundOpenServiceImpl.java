package com.szmsd.delivery.service.wrapper.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundOpenService;
import com.szmsd.delivery.service.wrapper.ShipmentEnum;
import com.szmsd.delivery.util.Utils;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.ShipmentUpdateRequestDto;
import com.szmsd.track.api.feign.TrackFeignService;
import com.szmsd.track.domain.Track;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
    private TrackFeignService delTrackService;

    @Resource
    private BasSellerFeignService basSellerFeignService;
    @Override
    public int shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
        try {
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("400", "???????????????");
            }
            // ??????????????????
            int result;
            boolean isPackingMaterial = dto.isPackingMaterial();
            if (isPackingMaterial) {
                result = this.delOutboundService.shipmentPackingMaterial(dto);
            } else {
                String orderType = delOutbound.getOrderType();
                boolean overBreak = false;
                // ?????????????????????????????????
                if ((DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(orderType) || DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(orderType))
                        // ????????????????????????
                        && "076002".equals(delOutbound.getPackageConfirm())) {
                    // ????????????
                    double weight = Utils.defaultValue(dto.getWeight());
                    // ?????????????????????
                    double weight1 = Utils.defaultValue(delOutbound.getWeight());
                    long deviation = Utils.defaultValue(delOutbound.getPackageWeightDeviation());
                    // ?????????????????????????????????
                    double abs = Math.abs(weight - weight1);
                    if (abs > deviation) {
                        // ???????????????????????????
                        overBreak = true;
                        // ???????????????????????????
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String templateFormat = "????????????{0}g?????????????????????{1}g????????????{2}g?????????????????????{3}g?????????????????????????????????????????????";
                        String exRemark = MessageFormat.format(templateFormat, weight, weight1, abs, deviation);
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                } else if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                    // ??????????????????????????????
                    // ??????????????????????????????????????????
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isEmpty(attachmentList)) {
                            // ??????????????????????????????????????????
                            overBreak = true;
                            // ??????????????????????????????????????????
                            ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                            shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                            shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
                            shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                            shipmentUpdateRequestDto.setIsEx(true);
                            shipmentUpdateRequestDto.setExType("OutboundIntercept");
                            String exRemark = "??????????????????????????????????????????";
                            shipmentUpdateRequestDto.setExRemark(exRemark);
                            shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                            this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                        }
                    }
                }
                // ??????????????????
                result = this.delOutboundService.shipmentPacking(dto, orderType, null);
                // ??????????????????0????????????????????????
                if (result > 0 && !overBreak) {
                    // ??????????????????
                    // EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
                    // ????????????????????????????????????????????????????????????
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
                throw new CommonException("400", "???????????????");
            }
            if (logger.isInfoEnabled()) {
                logger.info("======?????????????????????{}", delOutbound);
            }
            boolean overBreak = false;
            String orderType = delOutbound.getOrderType();
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                if (logger.isInfoEnabled()) {
                    logger.info("======??????????????????????????????????????????????????????");
                }
                // ??????????????????????????????
                // ??????????????????????????????????????????
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                if (logger.isInfoEnabled()) {
                    logger.info("======?????????????????????{}", listR);
                }
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isEmpty(attachmentList)) {
                        // ??????????????????????????????????????????
                        overBreak = true;
                        // ??????????????????????????????????????????
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
                        String exRemark = "??????????????????????????????????????????";
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                }
            }
            DelOutboundOperationLogEnum.OPN_CONTAINERS.listener(delOutbound);
            // ??????????????????
            this.delOutboundService.shipmentContainers(dto);
            // ??????????????????
            if (!overBreak) {
                // EventUtil.publishEvent(new ShipmentPackingEvent(delOutbound.getId()));
                // ????????????????????????????????????????????????????????????
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
            if (dto.getWeight() != null) {
                dto.setWeight(new BigDecimal(dto.getWeight()).add(new BigDecimal(2)).doubleValue());
            }
            String orderNo = dto.getOrderNo();
            queryWrapper.eq(DelOutbound::getOrderNo, orderNo);
            DelOutbound delOutbound = this.delOutboundService.getOne(queryWrapper);
            if (null == delOutbound) {
                throw new CommonException("400", "???????????????");
            }


            //??????????????????????????????
            R<BasSellerInfoVO> info = basSellerFeignService.getInfoBySellerCode(delOutbound.getSellerCode());
            if(info.getData() != null) {
                BasSellerInfoVO userInfo = R.getDataAndException(info);
                if(StringUtils.isNotEmpty(userInfo.getRulerCustomized())){
                    String[] lwh = StringUtils.split(userInfo.getRulerCustomized(), "*");
                    if(lwh.length >= 3){
                        dto.setLength(Double.parseDouble(lwh[0]));
                        dto.setWidth(Double.parseDouble(lwh[1]));
                        dto.setHeight(Double.parseDouble(lwh[2]));
                    }
                }
            }

            if (logger.isInfoEnabled()) {
                logger.info("======?????????????????????{}", delOutbound);
            }
            boolean overBreak = false;
            String orderType = delOutbound.getOrderType();
            if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(orderType) && delOutbound.getIsLabelBox()) {
                if (logger.isInfoEnabled()) {
                    logger.info("======??????????????????????????????????????????????????????");
                }
                // ??????????????????????????????
                // ??????????????????????????????????????????
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = this.attachmentService.list(basAttachmentQueryDTO);
                if (logger.isInfoEnabled()) {
                    logger.info("======?????????????????????{}", listR);
                }
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isEmpty(attachmentList)) {
                        // ??????????????????????????????????????????
                        overBreak = true;
                        // ??????????????????????????????????????????
                        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
                        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
                        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
                            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
                        } else {
                            String shipmentRule;
                            if (StringUtils.isNotEmpty(delOutbound.getProductShipmentRule())) {
                                shipmentRule = delOutbound.getProductShipmentRule();
                            } else {
                                shipmentRule = delOutbound.getShipmentRule();
                            }
                            shipmentUpdateRequestDto.setShipmentRule(shipmentRule);
                        }
                        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
                        shipmentUpdateRequestDto.setIsEx(true);
                        shipmentUpdateRequestDto.setExType("OutboundIntercept");
                        String exRemark = "??????????????????????????????????????????";
                        shipmentUpdateRequestDto.setExRemark(exRemark);
                        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
                        this.htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
                    }
                }
            }
            DelOutboundOperationLogEnum.OPN_CONTAINERS.listener(delOutbound);
            // ??????????????????
            this.delOutboundService.shipmentPacking(dto, null, !overBreak? ShipmentEnum.BEGIN: null);
            // ??????????????????
            if (!overBreak) {
                // ????????????????????????????????????????????????????????????
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            }
            delTrackService.addData(new Track()
                    .setOrderNo(delOutbound.getOrderNo())
                    .setTrackingNo(delOutbound.getTrackingNo())
                    .setTrackingStatus("Hub")
                    .setDescription("DMF, Parcel is being processed at the "+delOutbound.getWarehouseCode()));
            return 1;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
    }
}
