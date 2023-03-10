package com.szmsd.delivery.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelCk1OutboundDto;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.event.DelCk1RequestLogEvent;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.service.IDelOutboundBringVerifyAsyncService;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.ApplicationContainer;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.http.util.DomainInterceptorUtil;
import com.szmsd.pack.api.feign.PackageDeliveryConditionsFeignService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import com.szmsd.track.api.feign.TrackFeignService;
import com.szmsd.track.domain.Track;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class DelOutboundBringVerifyAsyncServiceImpl implements IDelOutboundBringVerifyAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundBringVerifyAsyncServiceImpl.class);

    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundRetryLabelService delOutboundRetryLabelService;

    @Autowired
    private TrackFeignService delTrackService;
    @SuppressWarnings({"all"})
    @Autowired
    private PackageDeliveryConditionsFeignService packageDeliveryConditionsFeignService;

    @Override
    public void bringVerifyAsync(String orderNo) {
        String key = applicationName + ":DelOutbound:bringVerifyAsync:" + orderNo;
        RLock lock = this.redissonClient.getLock(key);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                DelOutbound delOutbound = delOutboundService.getByOrderNo(orderNo);
                // ????????????????????????????????????????????????????????????
                if (DelOutboundStateEnum.REVIEWED_DOING.getCode().equals(delOutbound.getState())
                        || DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState())) {
                    bringVerifyAsync(delOutbound, AsyncThreadObject.build());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "?????????????????????" + e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void bringVerifyAsync(DelOutbound delOutbound, AsyncThreadObject asyncThreadObject) {
        StopWatch stopWatch = new StopWatch();
        Thread thread = Thread.currentThread();
        // ????????????
        long startTime = System.currentTimeMillis();
        boolean isAsyncThread = !asyncThreadObject.isAsyncThread();
        logger.info("(1)??????????????????????????????????????????{}???????????????ID???{}???????????????????????????{}????????????????????????{}", thread.getName(), thread.getId(), isAsyncThread, JSON.toJSONString(asyncThreadObject));
        if (isAsyncThread) {
            asyncThreadObject.loadTid();
        }
        stopWatch.start();
        DelOutboundWrapperContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        stopWatch.stop();
        logger.info(">>>>>[???????????????{}]????????????????????? ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        BringVerifyEnum currentState;
        String bringVerifyState = delOutbound.getBringVerifyState();
        if (StringUtils.isEmpty(bringVerifyState)) {
            currentState = BringVerifyEnum.BEGIN;
        } else {
            currentState = BringVerifyEnum.get(bringVerifyState);
            // ??????
            if (null == currentState) {
                currentState = BringVerifyEnum.BEGIN;
            }
        }
        logger.info("(2)??????????????????????????????????????????{}", delOutbound.getOrderNo());
        ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, BringVerifyEnum.END, BringVerifyEnum.BEGIN);
        try {
            // ????????????????????????
            // this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.REVIEWED_DOING);
            applicationContainer.action();
            // ?????????????????????CK1??????
            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())
                    || DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
                DelCk1OutboundDto ck1OutboundDto = new DelCk1OutboundDto();
                ck1OutboundDto.setWarehouseId(Ck1DomainPluginUtil.wrapper(delOutbound.getWarehouseCode()));
                DelCk1OutboundDto.PackageDTO packageDTO = new DelCk1OutboundDto.PackageDTO();
                packageDTO.setPackageId(delOutbound.getOrderNo());
                // packageDTO.setServiceCode(delOutbound.getShipmentRule());
                packageDTO.setServiceCode("DMTCK");
                DelCk1OutboundDto.PackageDTO.ShipToAddressDTO shipToAddressDTO = new DelCk1OutboundDto.PackageDTO.ShipToAddressDTO();
                DelOutboundAddress outboundAddress = context.getAddress();
                if(outboundAddress != null) {
                    shipToAddressDTO.setCountry(outboundAddress.getCountry());
                    shipToAddressDTO.setProvince(outboundAddress.getStateOrProvince());
                    shipToAddressDTO.setCity(outboundAddress.getCity());
                    shipToAddressDTO.setStreet1(outboundAddress.getStreet1());
                    shipToAddressDTO.setStreet2(outboundAddress.getStreet2());
                    shipToAddressDTO.setPostcode(outboundAddress.getPostCode());
                    shipToAddressDTO.setContact(outboundAddress.getConsignee());
                    shipToAddressDTO.setPhone(outboundAddress.getPhoneNo());
                    shipToAddressDTO.setEmail(outboundAddress.getEmail());
                }
                packageDTO.setShipToAddress(shipToAddressDTO);
                List<DelCk1OutboundDto.PackageDTO.SkusDTO> skusDTOList = new ArrayList<>();
                List<DelOutboundDetail> detailList = context.getDetailList();
                List<BaseProduct> productList = context.getProductList();
                Map<String, BaseProduct> productMap = null;
                if (CollectionUtils.isNotEmpty(productList)) {
                    productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
                }
                for (DelOutboundDetail outboundDetail : detailList) {
                    DelCk1OutboundDto.PackageDTO.SkusDTO skusDTO = new DelCk1OutboundDto.PackageDTO.SkusDTO();
                    String sku = outboundDetail.getSku();
                    // String inventoryCode = CkConfig.genCk1SkuInventoryCode(delOutbound.getSellerCode(), delOutbound.getWarehouseCode(), sku);
                    // skusDTO.setSku(inventoryCode);
                    skusDTO.setSku(sku);
                    skusDTO.setQuantity(outboundDetail.getQty());
                    if (null != productMap && null != productMap.get(sku)) {
                        BaseProduct baseProduct = productMap.get(sku);
                        skusDTO.setProductName(baseProduct.getProductName());
                        skusDTO.setPrice(baseProduct.getDeclaredValue());
                        skusDTO.setPlatformItemId("" + baseProduct.getId());
                    } else {
                        skusDTO.setProductName(outboundDetail.getProductName());
                        skusDTO.setPrice(outboundDetail.getDeclaredValue());
                    }
                    skusDTO.setHsCode(outboundDetail.getHsCode());
                    skusDTOList.add(skusDTO);
                }
                packageDTO.setSkus(skusDTOList);
                ck1OutboundDto.setPackage(packageDTO);
                DelCk1RequestLog ck1RequestLog = new DelCk1RequestLog();
                Map<String, String> headers = new HashMap<>();
                headers.put(DomainInterceptorUtil.KEYWORD, delOutbound.getSellerCode());
                ck1RequestLog.setRemark(JSON.toJSONString(headers));
                ck1RequestLog.setOrderNo(delOutbound.getOrderNo());
                ck1RequestLog.setRequestBody(JSON.toJSONString(ck1OutboundDto));
                ck1RequestLog.setType(DelCk1RequestLogConstant.Type.create.name());
                EventUtil.publishEvent(new DelCk1RequestLogEvent(ck1RequestLog));
            }
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                // ????????????????????????????????????????????????????????????
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            }

            String productCode = delOutbound.getShipmentRule();
            String prcProductCode = context.getPrcProductCode();
            if (com.szmsd.common.core.utils.StringUtils.isNotEmpty(prcProductCode)) {
                productCode = prcProductCode;
            }

            boolean bool = false;
            // ??????????????????
            if (StringUtils.isNotEmpty(delOutbound.getWarehouseCode())
                    && StringUtils.isNotEmpty(productCode)) {
                PackageDeliveryConditions packageDeliveryConditions = new PackageDeliveryConditions();
                packageDeliveryConditions.setWarehouseCode(delOutbound.getWarehouseCode());
                packageDeliveryConditions.setProductCode(productCode);
                R<PackageDeliveryConditions> packageDeliveryConditionsR = this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions);
                logger.info("?????????{}??????????????????????????????{}???{}, ??????json:{}", delOutbound.getOrderNo(), delOutbound.getWarehouseCode(), delOutbound.getShipmentService(),
                        JSONUtil.toJsonStr(packageDeliveryConditionsR));
                PackageDeliveryConditions packageDeliveryConditionsRData = null;
                if (null != packageDeliveryConditionsR && Constants.SUCCESS == packageDeliveryConditionsR.getCode()) {
                    packageDeliveryConditionsRData = packageDeliveryConditionsR.getData();
                }
                if (null != packageDeliveryConditionsRData && "AfterMeasured".equals(packageDeliveryConditionsRData.getCommandNodeCode())) {
                    //????????????????????????????????? ????????????????????????
                    bool = true;
                }
            }else{
                logger.info("?????????{}????????????????????????{}???{}", delOutbound.getOrderNo(), delOutbound.getWarehouseCode(), delOutbound.getShipmentService());
            }
            if(!bool){
                // ?????????????????????????????????
                delOutboundRetryLabelService.saveAndPushLabel(delOutbound.getOrderNo(), "pushLabel", "bringVerify");
            }

            Track delTrack = new Track();
            delTrack.setOrderNo(delOutbound.getOrderNo());
            delTrack.setTrackingNo(delOutbound.getTrackingNo());
            delTrack.setTrackingStatus("Todo");
            delTrack.setDescription("DMF, Parcel Infomation Received");
            delTrackService.addData(delTrack);
            logger.info("(3)??????????????????????????????????????????{}", delOutbound.getOrderNo());
        } catch (CommonException e) {
            // ????????????
            applicationContainer.setEndState(BringVerifyEnum.BEGIN);
            applicationContainer.rollback();
            // ???rollback?????????????????????BringVerifyState??????Begin??????????????????????????????????????????
            // ????????????
            // DelOutbound updateDelOutbound = new DelOutbound();
            // updateDelOutbound.setId(delOutbound.getId());
            // updateDelOutbound.setBringVerifyState(BringVerifyEnum.BEGIN.name());
            // this.delOutboundService.updateById(updateDelOutbound);
            // ????????????????????????????????????????????????
            // ????????????????????????????????????????????????
            this.logger.error("(4)??????????????????????????????????????????" + delOutbound.getOrderNo() + "??????????????????" + e.getMessage(), e);
            // ????????????
            // ???????????????????????????????????????
            // throw e;
            boolean partnerDeleteOrderFlag = false;
            String partnerCode = delOutbound.getPartnerCode();
//            if (StringUtils.isNotEmpty(partnerCode)) {
//                try {
//                    BasPartner queryBasPartner = new BasPartner();
//                    queryBasPartner.setPartnerCode(partnerCode);
//                    R<BasPartner> basPartnerR = this.partnerFeignService.getByCode(queryBasPartner);
//                    if (null != basPartnerR) {
//                        BasPartner basPartner = basPartnerR.getData();
//                        if (null != basPartner && (partnerDeleteOrderFlag = isTrue(basPartner.getDeleteOrderFlag()))) {
//                            // ??????????????????????????????
//                            this.delOutboundService.deleteFlag(delOutbound);
//                        }
//                    }
//                } catch (Exception e2) {
//                    logger.error(e2.getMessage(), e2);
//                }
//            }
            logger.info("(5)??????????????????????????????????????????????????????partnerCode: {}, partnerDeleteOrderFlag: {}", partnerCode, partnerDeleteOrderFlag);
            if("408".equals(e.getCode()) || "[408]????????????".equals(e.getMessage()) || "412".equals(e.getCode())){
                throw new RuntimeException(e);
            }
        } finally {
            if (isAsyncThread) {
                asyncThreadObject.unloadTid();
            }
        }
        this.logger.info("(5)????????????????????????????????????{}??????????????????{}", delOutbound.getOrderNo(), (System.currentTimeMillis() - startTime));
    }

    private boolean isTrue(Boolean value) {
        return null != value && value;
    }

}
