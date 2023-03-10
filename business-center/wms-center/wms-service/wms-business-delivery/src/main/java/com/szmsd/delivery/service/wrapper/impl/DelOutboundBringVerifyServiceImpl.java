package com.szmsd.delivery.service.wrapper.impl;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.chargerules.domain.BasProductService;
import com.szmsd.chargerules.enums.RecevieWarehouseStatusEnum;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.BigDecimalUtil;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.MessageUtil;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.dto.DelOutboundBringVerifyNoDto;
import com.szmsd.delivery.dto.DelOutboundFurtherHandlerDto;
import com.szmsd.delivery.dto.DelOutboundLabelDto;
import com.szmsd.delivery.enums.*;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.service.*;
import com.szmsd.delivery.service.impl.DelOutboundServiceImplUtil;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.PdfUtil;
import com.szmsd.delivery.util.Utils;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.delivery.vo.DelOutboundCombinationVO;
import com.szmsd.delivery.vo.DelOutboundPackingDetailVO;
import com.szmsd.delivery.vo.DelOutboundPackingVO;
import com.szmsd.ec.dto.TransferCallbackDTO;
import com.szmsd.ec.feign.CommonOrderFeignService;
import com.szmsd.exception.api.service.ExceptionInfoClientService;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpIBasClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Package;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.BaseOperationResponse;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.pack.api.feign.PackageDeliveryConditionsFeignService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-03-23 16:33
 */
@Service
public class DelOutboundBringVerifyServiceImpl implements IDelOutboundBringVerifyService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundBringVerifyServiceImpl.class);

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundAddressService delOutboundAddressService;
    @Autowired
    private IDelOutboundDetailService delOutboundDetailService;
    @Autowired
    private IHtpPricedProductClientService htpPricedProductClientService;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;
    @Autowired
    private IDelOutboundPackingService delOutboundPackingService;
    @Autowired
    private IDelOutboundCombinationService delOutboundCombinationService;
    @SuppressWarnings({"all"})
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private ExceptionInfoClientService exceptionInfoClientService;
    @SuppressWarnings({"all"})
    @Autowired
    private PackageDeliveryConditionsFeignService packageDeliveryConditionsFeignService;
    @Autowired
    private CommonOrderFeignService commonOrderFeignService;

    @Autowired
    private ChargeFeignService chargeFeignService;


    @Autowired
    private Environment env;



    @Override
    public void updateShipmentLabel(List<String> ids) {
        // ??????id??????????????????
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        delOutboundList.forEach(delOutbound -> {
            ApplicationContext context = this.initContext(delOutbound);
            //??????????????????????????????
            ShipmentEnum currentState;
            String shipmentState = delOutbound.getShipmentState();
            if (StringUtils.isEmpty(shipmentState)) {
                currentState = ShipmentEnum.BEGIN;
            } else {
                currentState = ShipmentEnum.get(shipmentState);
            }
            ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, ShipmentEnum.END, ShipmentEnum.BEGIN);
            applicationContainer.action();
        });
    }

    private List<DelOutboundBringVerifyVO> bringVerifyProcess(List<DelOutbound> delOutboundList){
        StopWatch stopWatch = new StopWatch();

        if (CollectionUtils.isEmpty(delOutboundList)) {
            throw new CommonException("400", MessageUtil.to("???????????????", "The document does not exist"));
        }
        List<DelOutboundBringVerifyVO> resultList = new ArrayList<>();
        for (DelOutbound delOutbound : delOutboundList) {
            if(StringUtils.isNotBlank(delOutbound.getShipmentRule())){
                PackageDeliveryConditions packageDeliveryConditions = new PackageDeliveryConditions();
                packageDeliveryConditions.setWarehouseCode(delOutbound.getWarehouseCode());
                packageDeliveryConditions.setProductCode(delOutbound.getShipmentRule());
                stopWatch.start();
                R<PackageDeliveryConditions> packageDeliveryConditionsR = this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions);
                stopWatch.stop();
                logger.info(">>>>>[???????????????{}]??????????????????????????????this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions)"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());

                if(packageDeliveryConditionsR != null && packageDeliveryConditionsR.getCode() == 200){
                    if(packageDeliveryConditionsR.getData() == null || !"1".equals(packageDeliveryConditionsR.getData().getStatus())){
                        throw new CommonException("400", delOutbound.getShipmentRule()+ MessageUtil.to("?????????????????????", "Logistics service is not effective:"+delOutbound.getOrderNo()));
                    }
                }

            }
            try {
                if (Objects.isNull(delOutbound)) {
                    throw new CommonException("400", MessageUtil.to("???????????????", "The document does not exist"));
                }
                // ????????????????????????????????????????????????
                boolean isAuditFailed = DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState());
                if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || isAuditFailed)) {
                    throw new CommonException("400", MessageUtil.to("????????????????????????????????????", "The document status is incorrect and cannot be submitted for approval"));
                }

                // ?????????????????????????????????
//                if(DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())){
//                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
//                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
//                basAttachmentQueryDTO.setAttachmentType(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getAttachmentType());
//                R<List<BasAttachment>> list = remoteAttachmentService.list(basAttachmentQueryDTO);
//                List<BasAttachment> dataAndException = R.getDataAndException(list);
//                if(dataAndException.size() == 0) {
//                    throw new CommonException("400", delOutbound.getOrderNo() + "????????????????????????????????????");
//                }
//                }
                // ????????????????????????
                /*AsyncThreadObject asyncThreadObject = AsyncThreadObject.build();
                this.delOutboundBringVerifyAsyncService.bringVerifyAsync(delOutbound, asyncThreadObject);*/
                // ????????????????????????????????????????????????????????????

                stopWatch.start();
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
                stopWatch.stop();
                logger.info(">>>>>[???????????????{}]?????????????????????:"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());

                stopWatch.start();


                // ????????????????????????
                this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.REVIEWED_DOING);
                stopWatch.stop();
                logger.info(">>>>>[???????????????{}]???????????????????????????:"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());
                resultList.add(new DelOutboundBringVerifyVO(delOutbound.getOrderNo(), true, MessageUtil.to("????????????", "Processed successfully")));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);

                DelOutbound updateDelOutbound = new DelOutbound();
                updateDelOutbound.setId(delOutbound.getId());

                String remark = e.getMessage();
                if (remark != null && remark.length() > 200) {
                    remark = remark.substring(0, 200);
                }
                updateDelOutbound.setExceptionMessage(remark);
                if(remark != null){
                    this.delOutboundService.updateByIdTransactional(updateDelOutbound);
                }
                if (null != delOutbound) {
                    resultList.add(new DelOutboundBringVerifyVO(delOutbound.getOrderNo(), false, e.getMessage()));
                } else {
                    resultList.add(new DelOutboundBringVerifyVO(MessageUtil.to("????????????", "Unknown order No"), false, e.getMessage()));
                }
            }
        }
        return resultList;
    }
    @Override
    public List<DelOutboundBringVerifyVO> bringVerify(DelOutboundBringVerifyDto dto) {
        List<Long> ids = dto.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new CommonException("400", MessageUtil.to("????????????????????????", "The request parameter cannot be empty"));
        }
        // ??????id??????????????????
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        return this.bringVerifyProcess(delOutboundList);
    }

    @Override
    public DelOutboundWrapperContext initContext(DelOutbound delOutbound) {

        return initContext(delOutbound, null);
    }

    @Override
    public DelOutboundWrapperContext initContext(DelOutbound delOutbound, DelOutboundFurtherHandlerDto furtherHandlerDto) {
        StopWatch stopWatch = new StopWatch();

        String orderNo = delOutbound.getOrderNo();
        String warehouseCode = delOutbound.getWarehouseCode();
        // ??????????????????
        stopWatch.start();
        DelOutboundAddress address = this.delOutboundAddressService.getByOrderNo(orderNo);

        stopWatch.stop();
        logger.info(">>>>>[???????????????{}????????????] ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        if (null == address) {
            // ??????????????????????????????
            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())) {
                throw new CommonException("400", MessageUtil.to("???????????????????????????", "The receiving address information does not exist"));
            }
        }
        stopWatch.start();
        // ??????sku??????
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);


        if(furtherHandlerDto != null){

            if( furtherHandlerDto.getDelOutboundAddress() != null){
                BeanUtils.copyProperties(furtherHandlerDto.getDelOutboundAddress(), address);

            }
            if(furtherHandlerDto.getDetailList() != null){
                Map<Long, DelOutboundAddress> maps = furtherHandlerDto.getDetailList().stream().collect(Collectors.toMap(DelOutboundAddress::getId, Function.identity(), (key1, key2) -> key2));
                for (DelOutboundDetail detail: detailList){
                    if(maps.containsKey(detail.getId())){
                        BeanUtils.copyProperties(maps.get(detail.getId()), detail);
                    }
                }

            }

            //???????????????????????????????????????
            if(StringUtils.isNotEmpty(furtherHandlerDto.getShipmentRule())){
                delOutbound.setShipmentRule(furtherHandlerDto.getShipmentRule());
            }else{
                delOutbound.setShipmentRule(delOutbound.getShipmentRule());
            }

            delOutbound.setShipmentService(furtherHandlerDto.getShipmentService());

            if(StringUtils.isNotEmpty(furtherHandlerDto.getTrackingAcquireType())){
                delOutbound.setTrackingAcquireType(furtherHandlerDto.getTrackingAcquireType());
            }

        }
        stopWatch.stop();
        logger.info(">>>>>[???????????????{}??????sku??????] ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        stopWatch.start();

        // ??????????????????
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
        stopWatch.stop();

        logger.info(">>>>>[???????????????{}??????????????????] ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        if (null == warehouse) {
            throw new CommonException("400", MessageUtil.to("?????????????????????", "Warehouse information does not exist"));
        }
        // ????????????????????????????????????????????????
        BasRegionSelectListVO country = null;
        if (null != address) {
            stopWatch.start();
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
            stopWatch.stop();

            logger.info(">>>>>[???????????????{}????????????????????????????????????????????????] ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", MessageUtil.to("?????????????????????", "Country information does not exist"));
            }
        }
        // ??????sku??????
        List<BaseProduct> productList = null;
        if (CollectionUtils.isNotEmpty(detailList)) {
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            List<String> skus = new ArrayList<>();
            for (DelOutboundDetail detail : detailList) {
                skus.add(detail.getSku());
            }
            // conditionQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
            conditionQueryDto.setSkus(skus);
            // ????????????????????????sku????????????
            if (!DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
                stopWatch.start();
                productList = this.baseProductClientService.queryProductList(conditionQueryDto);
                stopWatch.stop();

                logger.info(">>>>>[???????????????{}????????????????????????sku????????????] ??????{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

                if (CollectionUtils.isEmpty(productList)) {
                    throw new CommonException("400", MessageUtil.to("??????SKU????????????", "Failed to query SKU information"));
                }
            }
        }
        // ??????sku????????????
        /*List<String> bindCodeList = new ArrayList<>();
        for (BaseProduct baseProduct : productList) {
            if (StringUtils.isEmpty(baseProduct.getBindCode())) {
                continue;
            }
            bindCodeList.add(baseProduct.getBindCode());
        }
        List<BasePacking> packingList = null;
        if (CollectionUtils.isNotEmpty(bindCodeList)) {
            conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(bindCodeList);
            packingList = this.basePackingClientService.queryPackingList(conditionQueryDto);
        }*/
        return new DelOutboundWrapperContext(delOutbound, address, detailList, warehouse, country, productList, null);
    }

    @Override
    public ResponseObject<ChargeWrapper, ProblemDetails> pricing(DelOutboundWrapperContext delOutboundWrapperContext, PricingEnum pricingEnum) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        // ??????????????????
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // ??????sku??????
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // ??????????????????
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // ????????????????????????????????????????????????
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // ??????sku??????
        List<BaseProduct> productList = delOutboundWrapperContext.getProductList();

        Integer  thridPartStatus = delOutbound.getThridPartStatus();

        logger.info("{},pricing,thridPartStatus:{}",delOutbound.getOrderNo(),thridPartStatus);

        if(thridPartStatus.equals(1)){
            delOutbound.setLength(1D);
            delOutbound.setWidth(1D);
            delOutbound.setHeight(1D);
        }

        logger.info("{},??????????????? :{}",delOutbound.getOrderNo(),delOutbound.getLength());

        // ????????????
        List<PackageInfo> packageInfos = new ArrayList<>();

        boolean orderTypeFlag = DelOutboundOrderTypeEnum.COLLECTION.getCode().equals(delOutbound.getOrderType())
                                || DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())
                                || DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())
                                || DelOutboundOrderTypeEnum.MULTIBOX.getCode().equals(delOutbound.getOrderType());

        if (orderTypeFlag) {
            if (PricingEnum.SKU.equals(pricingEnum)) {
                BigDecimal declaredValue = BigDecimal.ZERO;
                Long totalQuantity = 0L;
                for (DelOutboundDetail detail : detailList) {
                    if (null != detail.getDeclaredValue()) {

                        BigDecimal dvalue = BigDecimal.valueOf(detail.getDeclaredValue());
                        BigDecimal bqty = BigDecimal.valueOf(detail.getQty());
                        BigDecimal resultValue = BigDecimalUtil.setScale(dvalue.multiply(bqty));

                        declaredValue = declaredValue.add(resultValue);
                    }
                    totalQuantity += detail.getQty();
                }
//                if(StringUtils.equals(delOutboundWrapperContext.getBringVerifyFlag(), "0")){
//                    totalQuantity = 1L;
//                }
                packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                        new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm"),
                        Math.toIntExact(totalQuantity), delOutbound.getOrderNo(), declaredValue, ""));
            } else if (PricingEnum.PACKAGE.equals(pricingEnum)) {
                BigDecimal declareValue = BigDecimal.ZERO;
                Long totalQuantity = 0L;
                for (DelOutboundDetail detail : detailList) {
                    if (null != detail.getDeclaredValue()) {

                        BigDecimal dvalue = BigDecimal.valueOf(detail.getDeclaredValue());
                        BigDecimal bqty = BigDecimal.valueOf(detail.getQty());
                        BigDecimal resultValue = BigDecimalUtil.setScale(dvalue.multiply(bqty));

                        declareValue = declareValue.add(resultValue);
                    }
//                    if(StringUtils.equals(delOutboundWrapperContext.getBringVerifyFlag(), "0")){
//                        totalQuantity = 1L;
//                    }
                    totalQuantity += detail.getQty();
                }
                packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                        new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                        , Math.toIntExact(1), delOutbound.getOrderNo(), declareValue, ""));
            }
        }else if(DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())){

            if (PricingEnum.SKU.equals(pricingEnum)) {
                BigDecimal declaredValue = BigDecimal.ZERO;
                for (DelOutboundDetail detail : detailList) {
                    if (null != detail.getDeclaredValue()) {

                        BigDecimal dvalue = BigDecimal.valueOf(detail.getDeclaredValue());
                        BigDecimal bqty = BigDecimal.valueOf(detail.getQty());
                        BigDecimal resultValue = BigDecimalUtil.setScale(dvalue.multiply(bqty));

                        declaredValue = declaredValue.add(resultValue);
                    }
                }
                packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                        new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm"),
                        Math.toIntExact(1), delOutbound.getOrderNo(), declaredValue, ""));
            } else if (PricingEnum.PACKAGE.equals(pricingEnum)) {
                BigDecimal declareValue = BigDecimal.ZERO;
                Long totalQuantity = 0L;
                for (DelOutboundDetail detail : detailList) {
                    if (null != detail.getDeclaredValue()) {

                        BigDecimal dvalue = BigDecimal.valueOf(detail.getDeclaredValue());
                        BigDecimal bqty = BigDecimal.valueOf(detail.getQty());
                        BigDecimal resultValue = BigDecimalUtil.setScale(dvalue.multiply(bqty));

                        declareValue = declareValue.add(resultValue);
                    }
//                    if(StringUtils.equals(delOutboundWrapperContext.getBringVerifyFlag(), "0")){
//                        totalQuantity = 1L;
//                    }
                    totalQuantity += detail.getQty();
                }
                packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                        new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                        , Math.toIntExact(1), delOutbound.getOrderNo(), declareValue, ""));
            }

        } else {
            if (PricingEnum.SKU.equals(pricingEnum)) {
                // ?????????????????????
                Set<String> skus = new HashSet<>();
                for (DelOutboundDetail detail : detailList) {
                    // sku????????????
                    if (StringUtils.isNotEmpty(detail.getBindCode())) {
                        skus.add(detail.getBindCode());
                    }
                }
                Map<String, BaseProduct> bindCodeMap = null;
                if (!skus.isEmpty()) {
                    BaseProductConditionQueryDto baseProductConditionQueryDto = new BaseProductConditionQueryDto();
                    baseProductConditionQueryDto.setSkus(new ArrayList<>(skus));

                    Long s = System.currentTimeMillis();

                    List<BaseProduct> basProductList = this.baseProductClientService.queryProductList(baseProductConditionQueryDto);

                    Long e = System.currentTimeMillis();
                    logger.info(">>>>>[???????????????{}]-Pricing?????????????????????baseProductClientService.queryProductList ??????{}", e-s);

                    if (CollectionUtils.isNotEmpty(basProductList)) {
                        bindCodeMap = basProductList.stream().collect(Collectors.toMap(BaseProduct::getCode, v -> v, (v1, v2) -> v1));
                    }
                }
                if (null == bindCodeMap) {
                    bindCodeMap = Collections.emptyMap();
                }
                Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
                for (DelOutboundDetail detail : detailList) {
                    String sku = detail.getSku();
                    long qty = detail.getQty();
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", MessageUtil.to("??????SKU[" + sku + "]????????????",
                                "Failed to query SKU ["+ sku +"] information"));
                    }
                    BigDecimal declaredValue;
                    if (null != product.getDeclaredValue()) {
                        declaredValue = BigDecimal.valueOf(product.getDeclaredValue());
                    } else {
                        declaredValue = BigDecimal.ZERO;
                    }

                    BigDecimal resultDeclaredValue = BigDecimalUtil.setScale(declaredValue.multiply(BigDecimal.valueOf(qty)));

                    packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(product.getWeight()), "g"),
                            new Packing(Utils.valueOf(product.getLength()), Utils.valueOf(product.getWidth()), Utils.valueOf(product.getHeight()), "cm"),
                            Math.toIntExact(1), delOutbound.getOrderNo(), resultDeclaredValue, product.getProductAttribute()));
                    // ?????????????????????
                    String bindCode = detail.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode)) {
                        BaseProduct baseProduct = bindCodeMap.get(bindCode);
                        if (null == baseProduct) {
                            throw new CommonException("400",  MessageUtil.to(
                                    "??????SKU[" + sku + "]?????????[" + bindCode + "]????????????",
                                    "Failed to query the package material ["+bindCode+"] information of SKU ["+sku+"]"));
                        }
                        if (null != baseProduct.getDeclaredValue()) {
                            declaredValue = BigDecimal.valueOf(baseProduct.getDeclaredValue());
                        } else {
                            declaredValue = BigDecimal.ZERO;
                        }

                        BigDecimal resultDeclaredValue2 = BigDecimalUtil.setScale(declaredValue.multiply(BigDecimal.valueOf(qty)));

                        packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(baseProduct.getWeight()), "g"),
                                new Packing(Utils.valueOf(baseProduct.getLength()), Utils.valueOf(baseProduct.getWidth()), Utils.valueOf(baseProduct.getHeight()), "cm"),
                                Math.toIntExact(1), delOutbound.getOrderNo(), resultDeclaredValue2, ""));
                    }
                }
            } else if (PricingEnum.PACKAGE.equals(pricingEnum)) {
                // ???????????????WMS?????????????????????????????????
                if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {

                    Long s = System.currentTimeMillis();
                    // ??????????????????
                    List<DelOutboundPacking> packingList = this.delOutboundPackingService.packageListByOrderNo(delOutbound.getOrderNo(), DelOutboundPackingTypeConstant.TYPE_2);

                    Long e = System.currentTimeMillis();
                    logger.info(">>>>>[???????????????{}]-Pricing?????????????????????delOutboundPackingService.packageListByOrderNo ??????{}", e-s);

                    if (CollectionUtils.isEmpty(packingList)) {
                        throw new CommonException("400", MessageUtil.to("???????????????WMS?????????????????????",
                                "No packing information returned by WMS is found"));
                    }
                    for (DelOutboundPacking packing : packingList) {
                        packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(packing.getWeight()), "g"),
                                new Packing(Utils.valueOf(packing.getLength()), Utils.valueOf(packing.getWidth()), Utils.valueOf(packing.getHeight()), "cm")
                                , Math.toIntExact(1), packing.getPackingNo(), BigDecimal.ZERO, ""));
                    }
                } else {
                    BigDecimal declareValue = BigDecimal.ZERO;
                    Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
                    for (DelOutboundDetail detail : detailList) {
                        String sku = detail.getSku();
                        long qty = detail.getQty();
                        BaseProduct product = productMap.get(sku);
                        if (null == product) {
                            throw new CommonException("400", MessageUtil.to("??????SKU[" + sku + "]????????????", "Failed to query SKU ["+sku+"] information"));
                        }
                        BigDecimal productDeclaredValue;
                        if (null != product.getDeclaredValue()) {
                            productDeclaredValue = BigDecimal.valueOf(product.getDeclaredValue());
                        } else {
                            productDeclaredValue = BigDecimal.ZERO;
                        }

                        BigDecimal resultV = BigDecimalUtil.setScale(productDeclaredValue.multiply(BigDecimal.valueOf(qty)));

                        declareValue = declareValue.add(resultV);
                    }
                    packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                            new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                            , Math.toIntExact(1), delOutbound.getOrderNo(), declareValue, ""));
                }
            }
        }

        int recevieWarehouseStatus = 0;

        if(StringUtils.isNotEmpty(delOutbound.getShipmentRule())) {

            List<String> productCodeList = Arrays.asList(delOutbound.getShipmentRule());
            Long s = System.currentTimeMillis();
            R<List<BasProductService>> basProductServiceRs = chargeFeignService.selectBasProductService(productCodeList);
            Long e = System.currentTimeMillis();
            logger.info(">>>>>[???????????????{}]-Pricing?????????????????????chargeFeignService.selectBasProductService ??????{}", e-s);
            if (basProductServiceRs.getCode() == 200) {
                List<BasProductService> basProductServices = basProductServiceRs.getData();
                for (BasProductService basProductService : basProductServices) {
                    recevieWarehouseStatus = basProductService.getRecevieWarehouseStatus();
                    if (recevieWarehouseStatus == RecevieWarehouseStatusEnum.WAREHOUSESTATUS.getCode()) {
                        break;
                    }
                }
            }
        }

        // ??????????????????
        CalcShipmentFeeCommand calcShipmentFeeCommand = new CalcShipmentFeeCommand();
        // true?????????????????????false??????????????????????????????
        calcShipmentFeeCommand.setAddressValifition(true);
        // ??????????????????????????????????????????
        calcShipmentFeeCommand.setProductCode(delOutbound.getShipmentRule());
        if (StringUtils.isNotEmpty(delOutbound.getCustomCode())) {
            calcShipmentFeeCommand.setClientCode(delOutbound.getCustomCode());
        } else {
            calcShipmentFeeCommand.setClientCode(delOutbound.getSellerCode());
        }
        calcShipmentFeeCommand.setShipmentType(delOutbound.getShipmentType());
        calcShipmentFeeCommand.setIoss(delOutbound.getIoss());
        calcShipmentFeeCommand.setPackageInfos(packageInfos);
        calcShipmentFeeCommand.setSheetCode(delOutbound.getSheetCode());

        Address toAddress =  new Address(address.getStreet1(),
                address.getStreet2(),
                address.getStreet3(),
                address.getPostCode(),
                address.getCity(),
                address.getStateOrProvince(),
                new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName())
        );

        Address fromAddress = new Address(warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getPostcode(),
                warehouse.getCity(),
                warehouse.getProvince(),
                new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName())
        );

        if(recevieWarehouseStatus == RecevieWarehouseStatusEnum.WAREHOUSESTATUS.getCode()){
            // ????????????
            calcShipmentFeeCommand.setToAddress(fromAddress);
            // ????????????
            calcShipmentFeeCommand.setFromAddress(toAddress);
        }else {
            // ????????????
            calcShipmentFeeCommand.setToAddress(toAddress);
            // ????????????
            calcShipmentFeeCommand.setFromAddress(fromAddress);
        }
        // ????????????
        calcShipmentFeeCommand.setToContactInfo(new ContactInfo(address.getConsignee(), address.getPhoneNo(), address.getEmail(), null));
        // calcShipmentFeeCommand.setCalcTimeForDiscount(new Date());
        Long s= System.currentTimeMillis();
        // ????????????
        ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> responseObjectWrapper = this.htpPricedProductClientService.pricing(calcShipmentFeeCommand);
        Long e = System.currentTimeMillis();
        logger.info(">>>>>[???????????????{}]-Pricing?????????????????????htpPricedProductClientService.pricing ??????{}", e-s);

        return responseObjectWrapper;
    }

    @Override
    public ShipmentOrderResult shipmentOrder(DelOutboundWrapperContext delOutboundWrapperContext) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        String orderNo = delOutbound.getOrderNo();
        String shipmentService = delOutbound.getShipmentService();
       /* if (StringUtils.isEmpty(shipmentService)) {
            throw new CommonException("400", MessageUtil.to("????????????????????????", "The delivery service name is empty"));
        }*/
        // ??????????????????
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // ??????sku??????
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // ??????????????????
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // ????????????????????????????????????????????????
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // ???????????????????????????
        CreateShipmentOrderCommand createShipmentOrderCommand = new CreateShipmentOrderCommand();
        createShipmentOrderCommand.setWarehouseCode(delOutbound.getWarehouseCode());
        String referenceNumber = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        // ??????uuid
        createShipmentOrderCommand.setReferenceNumber(referenceNumber);
        // ????????????refno
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            createShipmentOrderCommand.setOrderNumber(delOutbound.getRefNo());
        } else {
            createShipmentOrderCommand.setOrderNumber(orderNo);
        }

        Integer recevieWarehouseStatus = 0;

        if(StringUtils.isNotEmpty(delOutbound.getShipmentRule())) {

            List<String> productCodeList = Arrays.asList(delOutbound.getShipmentRule());
            Long s = System.currentTimeMillis();
            R<List<BasProductService>> basProductServiceRs = chargeFeignService.selectBasProductService(productCodeList);
            Long e = System.currentTimeMillis();
            logger.info(">>>>>[???????????????{}]??????????????? chargeFeignService.selectBasProductService ??????{}", e-s);

            if (basProductServiceRs.getCode() == 200) {
                List<BasProductService> basProductServices = basProductServiceRs.getData();
                for (BasProductService basProductService : basProductServices) {
                    recevieWarehouseStatus = basProductService.getRecevieWarehouseStatus();
                    if (recevieWarehouseStatus.equals(RecevieWarehouseStatusEnum.WAREHOUSESTATUS.getCode())) {
                        break;
                    }
                }
            }
        }

        AddressCommand addressCommand = new AddressCommand(address.getConsignee(),
                address.getPhoneNo(),
                address.getEmail(),
                address.getStreet1(),
                address.getStreet2(),
                address.getStreet3(),
                address.getCity(),
                address.getStateOrProvince(),
                address.getPostCode(),
                address.getCountryCode(),
                address.getTaxNumber(),
                address.getIdNumber(),
                delOutbound.getHouseNo()
        );

        AddressCommand resultAddressCommand = new AddressCommand(warehouse.getContact(),
                warehouse.getTelephone(),
                null,
                warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getCity(),
                warehouse.getProvince(),
                warehouse.getPostcode(),
                warehouse.getCountryCode(),
                null,
                null,
                null
        );
        
        createShipmentOrderCommand.setClientNumber(delOutbound.getSellerCode());

        if(recevieWarehouseStatus.equals(RecevieWarehouseStatusEnum.WAREHOUSESTATUS.getCode())){
            createShipmentOrderCommand.setReceiverAddress(resultAddressCommand);
            createShipmentOrderCommand.setReturnAddress(addressCommand);
        }else {
            createShipmentOrderCommand.setReceiverAddress(addressCommand);
            createShipmentOrderCommand.setReturnAddress(resultAddressCommand);
        }
        // ????????????
        String ioss = delOutbound.getIoss();
        if (StringUtils.isNotEmpty(ioss)) {
            List<Taxation> taxations = new ArrayList<>();
            taxations.add(new Taxation("IOSS", ioss));
            createShipmentOrderCommand.setTaxations(taxations);
        }
        createShipmentOrderCommand.setCodAmount(delOutbound.getCodAmount());
        // ????????????
        List<Package> packages = new ArrayList<>();
        List<PackageItem> packageItems = new ArrayList<>();
        int weightInGram = 0;
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            /*
            packageItems.add(new PackageItem(delOutbound.getOrderNo(), delOutbound.getOrderNo(), Utils.valueOf(delOutbound.getAmount()), weightInGram = Utils.valueOfDouble(delOutbound.getWeight()),
                    new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                    1, "", String.valueOf(delOutbound.getId()), delOutbound.getOrderNo()));

             */
            for (DelOutboundDetail detail : detailList) {
                Double declaredValue;
                if (null != detail.getDeclaredValue()) {
                    declaredValue = detail.getDeclaredValue();
                } else {
                    declaredValue = 0D;
                }
                packageItems.add(new PackageItem(detail.getProductName(), detail.getProductNameChinese(), declaredValue, 10,
                        new Size(1D, 1D, 1D),
                        Utils.valueOfLong(detail.getQty()), detail.getHsCode(), String.valueOf(detail.getId()), detail.getSku()));
            }
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            List<String> skus = new ArrayList<>();
            skus.add(delOutbound.getNewSku());
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            Long s = System.currentTimeMillis();
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            Long e = System.currentTimeMillis();
            logger.info(">>>>>[???????????????{}]??????????????? baseProductClientService.queryProductList ??????{}", e-s);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", MessageUtil.to("??????SKU[" + delOutbound.getNewSku() + "]????????????",
                        "Failed to query SKU ["+delOutbound. getNewSku()+"] information"));
            }
            BaseProduct product = productList.get(0);
            packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), weightInGram = Utils.valueOfDouble(product.getWeight()),
                    new Size(product.getLength(), product.getWidth(), product.getHeight()),
                    Utils.valueOfLong(delOutbound.getBoxNumber()), product.getHsCode(), String.valueOf(delOutbound.getId()), delOutbound.getNewSku()));
        } else if(DelOutboundOrderTypeEnum.MULTIBOX.getCode().equals(delOutbound.getOrderType())) {

            // ??????sku??????
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("??????SKU[" + sku + "]????????????",
                            "Failed to query SKU ["+sku+"] information"));
                }
                int productWeight = Utils.valueOfDouble(product.getWeight());
                weightInGram += productWeight;
                packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), productWeight,
                        new Size(product.getLength(), product.getWidth(), product.getHeight()),
                        Utils.valueOfLong(detail.getQty()), product.getHsCode(), String.valueOf(detail.getId()), sku));


                if (null != delOutbound.getWeight() && delOutbound.getWeight() > 0) {
                    weightInGram = Utils.valueOfDouble(delOutbound.getWeight());
                }
                String packageNumber = sku;
                packages.add(new Package(packageNumber, delOutbound.getRemark() + "|" + orderNo,
                        new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                        weightInGram, packageItems));

            }

            createShipmentOrderCommand.setPackages(packages);

        }else{
            // ??????sku??????
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("??????SKU[" + sku + "]????????????",
                            "Failed to query SKU ["+sku+"] information"));
                }
                int productWeight = Utils.valueOfDouble(product.getWeight());
                weightInGram += productWeight;
                packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), productWeight,
                        new Size(product.getLength(), product.getWidth(), product.getHeight()),
                        Utils.valueOfLong(detail.getQty()), product.getHsCode(), String.valueOf(detail.getId()), sku));
            }
        }
        if (null != delOutbound.getWeight() && delOutbound.getWeight() > 0) {
            weightInGram = Utils.valueOfDouble(delOutbound.getWeight());
        }
        String packageNumber;
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            packageNumber = delOutbound.getRefNo();
        } else {
            packageNumber = orderNo;
        }

        if(!DelOutboundOrderTypeEnum.MULTIBOX.getCode().equals(delOutbound.getOrderType())) {

            packages.add(new Package(packageNumber, delOutbound.getRemark() + "|" + orderNo,
                    new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                    weightInGram, packageItems));
            createShipmentOrderCommand.setPackages(packages);
        }

        createShipmentOrderCommand.setCarrier(new Carrier(shipmentService));
        Long s = System.currentTimeMillis();
        ResponseObject<ShipmentOrderResult, ProblemDetails> responseObjectWrapper = this.htpCarrierClientService.shipmentOrder(createShipmentOrderCommand);
        Long e = System.currentTimeMillis();
        logger.info(">>>>>[???????????????{}]??????????????? htpCarrierClientService.shipmentOrder ??????{}", e-s);
        if (null == responseObjectWrapper) {
            throw new CommonException("400", MessageUtil.to("??????????????????????????????????????????????????????????????????",
                    "Failed to create carrier logistics order, calling carrier system has no response"));
        }
        if (responseObjectWrapper.isSuccess()) {
            // ????????????
            // ?????????????????????????????????
            ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
            if (null == shipmentOrderResult) {
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(),
                        shipmentService, null, (MessageUtil.to("???????????????????????????????????????????????????????????????????????????",
                        "Failed to create the carrier logistics order. The data returned by calling the carrier system is blank")));
                throw new CommonException("400", MessageUtil.to("???????????????????????????????????????????????????????????????????????????",
                        "Failed to create the carrier logistics order. The data returned by calling the carrier system is blank"));
            }
            if (null == shipmentOrderResult.getSuccess() || !shipmentOrderResult.getSuccess()) {
                // ???????????????????????????
                ErrorDto error = shipmentOrderResult.getError();
                StringBuilder builder = new StringBuilder();
                if (null != error && StringUtils.isNotEmpty(error.getMessage())) {
                    if (StringUtils.isNotEmpty(error.getErrorCode())) {
                        builder.append("[")
                                .append(error.getErrorCode())
                                .append("]");
                    }
                    builder.append(error.getMessage());
                } else {
                    builder.append(MessageUtil.to("??????????????????????????????????????????????????????????????????????????????????????????",
                            "Failed to create the carrier logistics order, failed to call the carrier system, and the returned error message is blank"));
                }
                if(StringUtils.contains(builder.toString(), "????????????!????????????"+delOutbound.getOrderNo()+"?????????")){
                    logger.info("???????????????????????????{}???????????????1exceptionMessage???{}", delOutbound.getOrderNo(), builder.toString());
                    this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, delOutbound.getTrackingNo(), null);
                }else{
                    logger.info("???????????????????????????{}???????????????2exceptionMessage???{}", delOutbound.getOrderNo(), builder.toString());
                    this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, null, builder.toString());
                    throw new CommonException("412", builder.toString());
                }
            }
            this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, shipmentOrderResult.getMainTrackingNumber(), null);
            shipmentOrderResult.setReferenceNumber(referenceNumber);
            return shipmentOrderResult;
        } else {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError(), true), "???????????????????????????????????????????????????????????????");
            logger.info("???????????????????????????{}????????????exceptionMessage???{}", delOutbound.getOrderNo(), exceptionMessage);
            if(StringUtils.contains(exceptionMessage, "????????????!????????????"+delOutbound.getOrderNo()+"?????????")){
                //??????????????????????????????????????????????????????????????????
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, delOutbound.getTrackingNo(), exceptionMessage);
                return null;
            }else{
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, null, exceptionMessage);
                if("[408]????????????".equals(exceptionMessage)){
                    throw new CommonException("408", exceptionMessage);
                }
                throw new CommonException("400", exceptionMessage);
            }

        }
    }


    private void transferCallback(String orderNo, String shopifyOrderNo, String shipmentService, String mainTrackingNumber, String transferErrorMsg){
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            TransferCallbackDTO transferCallbackDTO = new TransferCallbackDTO();
            transferCallbackDTO.setOrderNo(shopifyOrderNo);
            transferCallbackDTO.setLogisticsRouteId(shipmentService);
            transferCallbackDTO.setTransferNumber(mainTrackingNumber);
            transferCallbackDTO.setTransferErrorMsg(transferErrorMsg);
            commonOrderFeignService.transferCallback(transferCallbackDTO);
            stopWatch.stop();
            logger.info(">>>>>[???????????????{}]?????????????????????{}", orderNo, stopWatch.getLastTaskInfo().getTimeMillis());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public ShipmentOrderResult shipmentAmazonOrder(DelOutboundWrapperContext delOutboundWrapperContext) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        String orderNo = delOutbound.getOrderNo();
        String shipmentService = delOutbound.getAmazonLogisticsRouteId();
        /*if (StringUtils.isEmpty(shipmentService)) {
            throw new CommonException("400", MessageUtil.to("????????????????????????", "The delivery service name is empty"));
        }*/
        // ??????????????????
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // ??????sku??????
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // ??????????????????
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // ????????????????????????????????????????????????
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // ???????????????????????????
        CreateShipmentOrderCommand createShipmentOrderCommand = new CreateShipmentOrderCommand();
//        createShipmentOrderCommand.setAmazonLogisticsRouteId(delOutbound.getAmazonLogisticsRouteId());
        createShipmentOrderCommand.setWarehouseCode(delOutbound.getWarehouseCode());
        // ??????uuid
        createShipmentOrderCommand.setReferenceNumber(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        // ????????????refno
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            createShipmentOrderCommand.setOrderNumber(delOutbound.getRefNo());
        } else {
            createShipmentOrderCommand.setOrderNumber(orderNo);
        }
        createShipmentOrderCommand.setClientNumber(delOutbound.getSellerCode());
        createShipmentOrderCommand.setReceiverAddress(new AddressCommand(address.getConsignee(),
                address.getPhoneNo(),
                address.getEmail(),
                address.getStreet1(),
                address.getStreet2(),
                address.getStreet3(),
                address.getCity(),
                address.getStateOrProvince(),
                address.getPostCode(),
                address.getCountryCode(),
                address.getTaxNumber(),
                address.getIdNumber(),
                delOutbound.getHouseNo()
        ));
        createShipmentOrderCommand.setReturnAddress(new AddressCommand(warehouse.getContact(),
                warehouse.getTelephone(),
                null,
                warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getCity(),
                warehouse.getProvince(),
                warehouse.getPostcode(),
                warehouse.getCountryCode(),
                null,
                null,
                null
            ));
        // ????????????
        String ioss = delOutbound.getIoss();
        if (StringUtils.isNotEmpty(ioss)) {
            List<Taxation> taxations = new ArrayList<>();
            taxations.add(new Taxation("IOSS", ioss));
            createShipmentOrderCommand.setTaxations(taxations);
        }
        createShipmentOrderCommand.setCodAmount(delOutbound.getCodAmount());
        // ????????????
        List<Package> packages = new ArrayList<>();
        List<PackageItem> packageItems = new ArrayList<>();
        int weightInGram = 0;
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            /*
            packageItems.add(new PackageItem(delOutbound.getOrderNo(), delOutbound.getOrderNo(), Utils.valueOf(delOutbound.getAmount()), weightInGram = Utils.valueOfDouble(delOutbound.getWeight()),
                    new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                    1, "", String.valueOf(delOutbound.getId()), delOutbound.getOrderNo()));

             */
            for (DelOutboundDetail detail : detailList) {
                Double declaredValue;
                if (null != detail.getDeclaredValue()) {
                    declaredValue = detail.getDeclaredValue();
                } else {
                    declaredValue = 0D;
                }
                packageItems.add(new PackageItem(detail.getProductName(), detail.getProductNameChinese(), declaredValue, 10,
                        new Size(1D, 1D, 1D),
                        Utils.valueOfLong(detail.getQty()), detail.getHsCode(), String.valueOf(detail.getId()), detail.getSku()));
            }
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            List<String> skus = new ArrayList<>();
            skus.add(delOutbound.getNewSku());
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", MessageUtil.to("??????SKU[" + delOutbound.getNewSku() + "]????????????",
                        "Failed to query SKU ["+delOutbound. getNewSku()+"] information"));            }
            BaseProduct product = productList.get(0);
            packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), weightInGram = Utils.valueOfDouble(product.getWeight()),
                    new Size(product.getLength(), product.getWidth(), product.getHeight()),
                    Utils.valueOfLong(delOutbound.getBoxNumber()), product.getHsCode(), String.valueOf(delOutbound.getId()), delOutbound.getNewSku()));
        } else {
            // ??????sku??????
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("??????SKU[" + sku + "]????????????",
                            "Failed to query SKU ["+sku+"] information"));                }
                int productWeight = Utils.valueOfDouble(product.getWeight());
                weightInGram += productWeight;
                packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), productWeight,
                        new Size(product.getLength(), product.getWidth(), product.getHeight()),
                        Utils.valueOfLong(detail.getQty()), product.getHsCode(), String.valueOf(detail.getId()), sku));
            }
        }
        if (null != delOutbound.getWeight() && delOutbound.getWeight() > 0) {
            weightInGram = Utils.valueOfDouble(delOutbound.getWeight());
        }
//        if (weightInGram <= 0) {
//            throw new CommonException("400", MessageUtil.to("???????????????0????????????0????????????????????????????????????",
//                    "If the package weight is 0 or less than 0, the carrier logistics order cannot be created"));        }
        String packageNumber;
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            packageNumber = delOutbound.getRefNo();
        } else {
            packageNumber = orderNo;
        }
        packages.add(new Package(packageNumber, delOutbound.getRemark() + "|" + orderNo,
                new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                weightInGram, packageItems));
        createShipmentOrderCommand.setPackages(packages);
        createShipmentOrderCommand.setCarrier(new Carrier(shipmentService));
        ResponseObject<ShipmentOrderResult, ProblemDetails> responseObjectWrapper = this.htpCarrierClientService.shipmentOrder(createShipmentOrderCommand);
        if (null == responseObjectWrapper) {
            throw new CommonException("400",MessageUtil.to("???????????????????????????????????????????????????????????????????????????",
                    "Failed to create Amazon carrier logistics order, calling carrier system has no response"

            ) );
        }
        if (!responseObjectWrapper.isSuccess()) {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError(), true), "???????????????????????????????????????????????????????????????");
            if("[408]????????????".equals(exceptionMessage)){
                throw new CommonException("408", exceptionMessage);
            }
            throw new CommonException("400", exceptionMessage);
        }
        // ????????????
        // ?????????????????????????????????
        ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
        if (null == shipmentOrderResult) {
            throw new CommonException("400", MessageUtil.to("????????????????????????????????????????????????????????????????????????????????????",
                    "Failed to create an Amazon carrier logistics order. The data returned by calling the carrier system is blank"));
        }
        if (null == shipmentOrderResult.getSuccess() || !shipmentOrderResult.getSuccess()) {
            // ???????????????????????????
            ErrorDto error = shipmentOrderResult.getError();
            StringBuilder builder = new StringBuilder();
            if (null != error && StringUtils.isNotEmpty(error.getMessage())) {
                if (StringUtils.isNotEmpty(error.getErrorCode())) {
                    builder.append("[")
                            .append(error.getErrorCode())
                            .append("]");
                }
                builder.append(error.getMessage());
            } else {
                builder.append(MessageUtil.to("???????????????????????????????????????????????????????????????????????????????????????????????????", "Failed to create an Amazon carrier logistics order, failed to call the carrier system, and the error returned is blank"));
            }
            throw new CommonException("400", builder.toString());
        }
        return shipmentOrderResult;
    }

    @Override
    public void shipmentRule(DelOutbound delOutbound) {
        String logMessage;
        // ??????PRC???????????????????????????????????????????????????????????????????????????????????????
        String shipmentRule = delOutbound.getProductShipmentRule();
        if (StringUtils.isEmpty(shipmentRule)) {
            shipmentRule = delOutbound.getShipmentRule();
        }
        // ????????????????????????????????????XiaoHui
        if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(shipmentRule)) {
            shipmentRule = "XiaoHui";
        }
        out:
        if (StringUtils.isEmpty(shipmentRule)) {
            logMessage = MessageUtil.to("??????????????????", "Shipment rule is empty");
        } else {
            // ??????????????????MD5???
            String shipmentRuleMd5 = MD5.create().digestHex(shipmentRule);
            String key = "Delivery:ShipmentRule:" + shipmentRuleMd5;
            Object o = this.redisTemplate.opsForValue().get(key);
            if (Objects.nonNull(o)) {
                logMessage = MessageUtil.to("??????????????????????????????WMS", "Shipment rule already exists Do not update WMS");
                break out;
            }
            logMessage = MessageUtil.to("??????????????????????????????WMS", "Shipment rule does not exist, update WMS");
            // ????????????/??????????????????
            AddShipmentRuleRequest addShipmentRuleRequest = new AddShipmentRuleRequest();
            addShipmentRuleRequest.setWarehouseCode(delOutbound.getWarehouseCode());
            addShipmentRuleRequest.setOrderNo(delOutbound.getOrderNo());
            String orderType = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getOrderType(), "");
            String shipmentChannel = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getShipmentChannel(), "");
            if (orderType.equals(DelOutboundOrderTypeEnum.BATCH.getCode()) && "SelfPick".equals(shipmentChannel)) {
                addShipmentRuleRequest.setShipmentRule(delOutbound.getDeliveryAgent());
                addShipmentRuleRequest.setGetLabelType(DelOutboundTrackingAcquireTypeEnum.NONE.getCode());
            } else {
                // ??????PRC?????????????????????????????????
                addShipmentRuleRequest.setShipmentRule(shipmentRule);
                addShipmentRuleRequest.setGetLabelType(delOutbound.getTrackingAcquireType());
            }
            IHtpIBasClientService htpIBasClientService = SpringUtils.getBean(IHtpIBasClientService.class);
            BaseOperationResponse baseOperationResponse = htpIBasClientService.shipmentRule(addShipmentRuleRequest);
            if (null == baseOperationResponse) {
                throw new CommonException("400", MessageUtil.to("??????/????????????????????????", "Failed to add/modify the shipment rule"));
            }
            if (null == baseOperationResponse.getSuccess()) {
                baseOperationResponse.setSuccess(false);
            }
            if (!baseOperationResponse.getSuccess()) {
                String msg = baseOperationResponse.getMessage();
                if (com.szmsd.common.core.utils.StringUtils.isEmpty(msg)) {
                    msg = baseOperationResponse.getErrors();
                }
                String message = Utils.defaultValue(msg, MessageUtil.to("??????/????????????????????????", "Failed to add/modify the shipment rule"));
                throw new CommonException("400", message);
            }
            // ????????????????????????redis??????????????????redis???????????????????????????WMS
            this.redisTemplate.opsForValue().set(key, shipmentRuleMd5);
        }
        Object[] params = new Object[]{delOutbound, logMessage};
        DelOutboundOperationLogEnum.BRV_SHIPMENT_RULE.listener(params);
    }
    @Override
    public String saveShipmentLabel(FileStream fileStream, DelOutbound delOutbound) {
        String orderNumber = delOutbound.getShipmentOrderNumber();
        String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound);
        File file = new File(pathname);
        if (!file.exists()) {
            try {
                FileUtils.forceMkdir(file);
            } catch (IOException e) {
                // ?????????????????????????????????????????????
                throw new CommonException("500", MessageUtil.to("???????????????[" + file.getPath() + "]?????????Error???" + e.getMessage()

                , "Failed to create folder ["+file. getPath()+"], Error:"+e.getMessage()));
            }
        }
        byte[] inputStream;
        if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
            String path  = file.getPath() + "/" + orderNumber + ".pdf";
            File labelFile = new File(file.getPath() + "/" + orderNumber + ".pdf");
            if (labelFile.exists()) {
                File destFile = new File(file.getPath() + "/" + orderNumber + "_" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss") + ".pdf");
                try {
                    FileUtils.copyFile(labelFile, destFile);
                } catch (IOException e) {
                    logger.error("?????????????????????" + e.getMessage(), e);
                }
            }
            try {
                FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                return path;
            } catch (IOException e) {
                // ?????????????????????????????????????????????
                throw new CommonException("500", MessageUtil.to("???????????????????????????Error???" + e.getMessage(),
                        "Failed to save label file, Error:"+e.getMessage())
                        );
            }
        }
        logger.error("???????????????????????????");
        throw new CommonException("500", MessageUtil.to("???????????????????????????", "Failed to get tag file stream"));

    }
    @Override
    public String getShipmentLabel(DelOutbound delOutbound) {
        if (null == delOutbound) {
            throw new CommonException("500",  MessageUtil.to("???????????????????????????", "Delivery list information cannot be empty"));
        }
        String orderNumber = delOutbound.getShipmentOrderNumber();
        if (StringUtils.isEmpty(orderNumber)) {
            return null;
        }
        // ????????????
        CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
        command.setWarehouseCode(delOutbound.getWarehouseCode());
        command.setOrderNumber(orderNumber);
        command.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
        DelOutboundOperationLogEnum.SMT_LABEL.listener(delOutbound);
        logger.info("????????????????????????????????????{}????????????????????????{}", delOutbound.getOrderNo(), orderNumber);
        ResponseObject<FileStream, ProblemDetails> responseObject = this.htpCarrierClientService.label(command);
        if (null != responseObject) {
            if (responseObject.isSuccess()) {
                FileStream fileStream = responseObject.getObject();
                String pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound);
                File file = new File(pathname);
                if (!file.exists()) {
                    try {
                        FileUtils.forceMkdir(file);
                    } catch (IOException e) {
                        // ?????????????????????????????????????????????
                        throw new CommonException("500", MessageUtil.to("???????????????[" + file.getPath() + "]?????????Error???" + e.getMessage()

                                , "Failed to create folder ["+file. getPath()+"], Error:"+e.getMessage()));                    }
                }
                byte[] inputStream;
                if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                    String path  = file.getPath() + "/" + orderNumber + ".pdf";
                    File labelFile = new File(file.getPath() + "/" + orderNumber + ".pdf");
                    if (labelFile.exists()) {
                        File destFile = new File(file.getPath() + "/" + orderNumber + "_" + DateFormatUtils.format(new Date(), "yyyyMMdd_HHmmss") + ".pdf");
                        try {
                            FileUtils.copyFile(labelFile, destFile);
                        } catch (IOException e) {
                            logger.error("?????????????????????" + e.getMessage(), e);
                        }
                    }
                    try {
                        FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                        return path;
                    } catch (IOException e) {
                        // ?????????????????????????????????????????????
                        throw new CommonException("500", MessageUtil.to("???????????????????????????Error???" + e.getMessage(),
                                "Failed to save label file, Error:"+e.getMessage())
                        );                    }
                }
            } else {
                // ?????????????????????????????????
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()),

                        MessageUtil.to("???????????????????????????2", "Failed to get tag file stream2"));
                logger.error(exceptionMessage);
                throw new CommonException("500", exceptionMessage);
            }
        } else {
            // ??????????????????????????????
            logger.error("???????????????????????????");
            throw new CommonException("500", MessageUtil.to("???????????????????????????", "Failed to get tag file stream"));
        }
        return null;
    }

    @Override
    public void htpShipmentLabel(DelOutbound delOutbound) {
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            return;
        }
        DelOutboundOperationLogEnum.SMT_SHIPMENT_LABEL.listener(delOutbound);


        String selfPickLabelFilePath = null;
        if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
            DelOutboundLabelDto dto = new DelOutboundLabelDto();
            dto.setId(delOutbound.getId());
            //???????????????????????????????????????????????????
            delOutboundService.labelSelfPick(null, dto);
            selfPickLabelFilePath = DelOutboundServiceImplUtil.getSelfPickLabelFilePath(delOutbound) + "/" + delOutbound.getOrderNo() + ".pdf";
        }



        String pathname = null;
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            // ??????????????????????????????
            String mergeFileDirPath = DelOutboundServiceImplUtil.getBatchMergeFilePath(delOutbound);
            File mergeFileDir = new File(mergeFileDirPath);
            if (!mergeFileDir.exists()) {
                try {
                    FileUtils.forceMkdir(mergeFileDir);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("????????????????????????", "Failed to create folder,") + e.getMessage());
                }
            }
            String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
            File mergeFile = new File(mergeFilePath);
            if (!mergeFile.exists()) {
                String boxFilePath = "";
                // ????????????
                if (delOutbound.getIsLabelBox()) {
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isNotEmpty(attachmentList)) {
                            BasAttachment attachment = attachmentList.get(0);
                            // ???????????? - ?????????
                            boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                        }
                    }
                }
                String labelFilePath = "";
                if ("SelfPick".equals(delOutbound.getShipmentChannel())) {
                    // ?????????????????????????????????????????????
                    // ?????????????????????
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> documentListR = remoteAttachmentService.list(basAttachmentQueryDTO);
                    if (null != documentListR && null != documentListR.getData()) {
                        List<BasAttachment> documentList = documentListR.getData();
                        if (CollectionUtils.isNotEmpty(documentList)) {
                            BasAttachment basAttachment = documentList.get(0);
                            labelFilePath = basAttachment.getAttachmentPath() + "/" + basAttachment.getAttachmentName() + basAttachment.getAttachmentFormat();
                        }
                    }
                    if (StringUtils.isEmpty(labelFilePath)) {
                        throw new CommonException("500", MessageUtil.to("?????????????????????", "The box label file is not uploaded"));
                    }
                } else {
                    // ???????????? - ?????????????????????????????????
                    labelFilePath = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";
                }
                String uploadBoxLabel = null;
                if("Y".equals(delOutbound.getUploadBoxLabel())){
                    uploadBoxLabel = getBoxLabel(delOutbound);
                }
                // ????????????
                try {
                    if (PdfUtil.merge(mergeFilePath, boxFilePath, labelFilePath, uploadBoxLabel, selfPickLabelFilePath)) {
                        pathname = mergeFilePath;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("???????????????????????????????????????","Merging box label file, label file failed"));
                }
            }else{
                pathname = mergeFilePath;
            }
        }
        if (null == pathname) {

            String boxFilePath = "";
            // ????????????
            if (delOutbound.getIsLabelBox()) {
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                        BasAttachment attachment = attachmentList.get(0);
                        // ???????????? - ?????????
                        boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                    }
                }
            }else{
                //????????????
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                        BasAttachment attachment = attachmentList.get(0);
                        // ???????????? - ?????????
                        boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                    }
                }
            }

            // ???????????????????????????????????????????????????
            pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";


            //??????????????????????????????????????????
            String uploadBoxLabel = null;
            if("Y".equals(delOutbound.getUploadBoxLabel())){
                uploadBoxLabel = getBoxLabel(delOutbound);
            }
            logger.info("???????????????{}??????,??????{},????????????{},??????{}",delOutbound.getOrderNo(), pathname, selfPickLabelFilePath, uploadBoxLabel);
            pathname = this.mergeFile(delOutbound, pathname, boxFilePath, selfPickLabelFilePath, uploadBoxLabel);

        }
        File labelFile = new File(pathname);
        // ??????????????????????????????
        if (labelFile.exists()) {
            try {
                
                String labelType = "ShipmentLabel";

                if(DelOutboundOrderTypeEnum.MULTIBOX.getCode().equals(delOutbound.getOrderType())){
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);

                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isNotEmpty(attachmentList)) {
                            BasAttachment attachment = attachmentList.get(0);
                            if(AttachmentTypeEnum.MULTIPLE_PIECES_INVOICE.getBusinessCode().equals(attachment.getBusinessCode())){
                                labelType = "ShipmentInvoice";
                            }

                        }
                    }
                }

                byte[] byteArray = FileUtils.readFileToByteArray(labelFile);
                String encode = cn.hutool.core.codec.Base64.encode(byteArray);
                ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
                shipmentLabelChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
                shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
                shipmentLabelChangeRequestDto.setLabelType(labelType);
                shipmentLabelChangeRequestDto.setLabel(encode);
                ResponseVO responseVO = htpOutboundClientService.shipmentLabel(shipmentLabelChangeRequestDto);
                if (null == responseVO || null == responseVO.getSuccess()) {
                    throw new CommonException("400", MessageUtil.to("????????????????????????????????????", "Failed to update the label. The request is unresponsive"));
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("400", MessageUtil.to("?????????????????????", "Failed to update label,") + Utils.defaultValue(responseVO.getMessage(), ""));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("500", "????????????????????????");
            }
        }
    }

    private String mergeFile(DelOutbound delOutbound, String... urls){
        String mergeFileDirPath = DelOutboundServiceImplUtil.getBatchMergeFilePath(delOutbound);
        File mergeFileDir = new File(mergeFileDirPath);
        if (!mergeFileDir.exists()) {
            try {
                FileUtils.forceMkdir(mergeFileDir);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("500", MessageUtil.to("????????????????????????", "Failed to create folder,") + e.getMessage());
            }
        }
        String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
        new File(mergeFilePath).deleteOnExit();
        logger.info("{}????????????wms????????????{}", delOutbound.getOrderNo(), urls);
            ;
        try {
            PdfUtil.merge(mergeFilePath, urls);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new CommonException("500", MessageUtil.to("???????????????????????????????????????","Merging box label file, label file failed"));
        }
        return mergeFilePath;
    }

    @Override
    public String getBoxLabel(DelOutbound delOutbound){
        BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
        basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.ONE_PIECE_ISSUED_ON_BEHALF.getBusinessCode());
        basAttachmentQueryDTO.setRemark(delOutbound.getOrderNo());
        R<List<BasAttachment>> documentListR = remoteAttachmentService.list(basAttachmentQueryDTO);
        if (null != documentListR && null != documentListR.getData() && CollectionUtils.isNotEmpty(documentListR.getData())) {
            List<BasAttachment> documentList = documentListR.getData();
            BasAttachment basAttachment = documentList.get(0);
            return basAttachment.getAttachmentPath() + "/" + basAttachment.getAttachmentName() + basAttachment.getAttachmentFormat();
        }else{
            basAttachmentQueryDTO = new BasAttachmentQueryDTO();
            basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.TRANSSHIPMENT_OUTBOUND.getBusinessCode());
            basAttachmentQueryDTO.setRemark(delOutbound.getOrderNo());
            documentListR = remoteAttachmentService.list(basAttachmentQueryDTO);
            List<BasAttachment> documentList = documentListR.getData();
            if (CollectionUtils.isNotEmpty(documentList)) {
                BasAttachment basAttachment = documentList.get(0);
                return basAttachment.getAttachmentPath() + "/" + basAttachment.getAttachmentName() + basAttachment.getAttachmentFormat();
            }
        }
        return null;
    }

    @Override
    public void shipmentShipping(DelOutbound delOutbound) {
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            return;
        }
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
        shipmentUpdateRequestDto.setIsEx(false);
        shipmentUpdateRequestDto.setExType(null);
        shipmentUpdateRequestDto.setExRemark(null);
        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
        ResponseVO responseVO = htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
        if (null == responseVO || null == responseVO.getSuccess()) {
            throw new CommonException("400", MessageUtil.to("??????????????????????????????????????????", "Failed to update the delivery instruction. The request has no response"));
        }
        if (!responseVO.getSuccess()) {
            throw new CommonException("400", MessageUtil.to("???????????????????????????", "Failed to update the delivery instruction") + Utils.defaultValue(responseVO.getMessage(), ""));
        }
    }

    @Override
    public void shipmentShippingEx(DelOutbound delOutbound, String exRemark) {
        if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
            return;
        }
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
        shipmentUpdateRequestDto.setExType("OutboundGetTrackingFailed");
        exRemark = ProblemDetails.getErrorMessageOrNullFormat(exRemark);
        logger.info("??????{}???shipmentShipping????????????{}", delOutbound.getOrderNo(), exRemark);
        shipmentUpdateRequestDto.setExRemark(Utils.defaultValue(exRemark, MessageUtil.to("????????????", "operation failed")));
        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
        htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
    }

    @Async
    @Override
    public void ignoreExceptionInfo(String orderNo) {
        try {
            int ignore = this.exceptionInfoClientService.ignore(orderNo);
            logger.info("?????????[{}]?????????????????????????????????ignore:{}", orderNo, ignore);
        } catch (Exception e) {
            logger.error("?????????[" + orderNo + "]???????????????????????????????????????" + e.getMessage(), e);
        }
    }

    @Override
    public void cancellation(String warehouseCode, String referenceNumber, String shipmentOrderNumber, String trackingNo) {
        CancelShipmentOrderCommand command = new CancelShipmentOrderCommand();
        command.setWarehouseCode(warehouseCode);
        command.setReferenceNumber(referenceNumber);
        List<CancelShipmentOrder> cancelShipmentOrders = new ArrayList<>();
        cancelShipmentOrders.add(new CancelShipmentOrder(shipmentOrderNumber, trackingNo));
        command.setCancelShipmentOrders(cancelShipmentOrders);
        ResponseObject<CancelShipmentOrderBatchResult, ErrorDataDto> responseObject = this.htpCarrierClientService.cancellation(command);
        if (null == responseObject || !responseObject.isSuccess()) {
            throw new CommonException("400", MessageUtil.to("?????????????????????????????????", "Failed to cancel the carrier logistics order"));
        }
        CancelShipmentOrderBatchResult cancelShipmentOrderBatchResult = responseObject.getObject();
        List<CancelShipmentOrderResult> cancelOrders = cancelShipmentOrderBatchResult.getCancelOrders();
        for (CancelShipmentOrderResult cancelOrder : cancelOrders) {
            if (!cancelOrder.isSuccess()) {
                throw new CommonException("400", MessageUtil.to("?????????????????????????????????", "Failed to cancel the carrier logistics order")+"2");
            }
        }
    }

    @Override
    public String shipmentCreate(DelOutboundWrapperContext delOutboundWrapperContext, String trackingNo) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        // ??????????????????
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // ??????sku??????
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // ????????????????????????????????????????????????
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // ?????????WMS
        CreateShipmentRequestDto createShipmentRequestDto = new CreateShipmentRequestDto();
        createShipmentRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
        createShipmentRequestDto.setOrderType(delOutbound.getOrderType());
        createShipmentRequestDto.setSellerCode(delOutbound.getSellerCode());
        createShipmentRequestDto.setTrackingNo(trackingNo);
        createShipmentRequestDto.setRefCode(delOutbound.getRefNo());
        // ?????????prc?????????????????????
        String shipmentRule;
        if (StringUtils.isNotEmpty(delOutbound.getProductShipmentRule())) {
            shipmentRule = delOutbound.getProductShipmentRule();
        } else {
            shipmentRule = delOutbound.getShipmentRule();
        }
        if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
            shipmentRule = "Com";
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            shipmentRule = "Spl";
        }
        // ????????????
        else if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(delOutbound.getOrderType())) {
            shipmentRule = "XiaoHui";
        }
        createShipmentRequestDto.setShipmentRule(shipmentRule);
        createShipmentRequestDto.setPackingRule(delOutbound.getPackingRule());
        boolean isBatchSelfPick = false;
        if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
            createShipmentRequestDto.setTrackingNo(delOutbound.getDeliveryInfo());
            createShipmentRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
        } else if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            if ("SelfPick".equals(delOutbound.getShipmentChannel())) {
                isBatchSelfPick = true;
                createShipmentRequestDto.setTrackingNo(delOutbound.getDeliveryInfo());
                createShipmentRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
            }
        }
        createShipmentRequestDto.setRemark(delOutbound.getRemark());
        createShipmentRequestDto.setRefOrderNo(delOutbound.getOrderNo());
        // ??????????????????????????????????????????????????????????????????
        if (null != address && !isBatchSelfPick) {
            createShipmentRequestDto.setAddress(new ShipmentAddressDto(address.getConsignee(),
                    address.getCountryCode(), country.getName(), address.getZone(), address.getStateOrProvince(), address.getCity(),
                    address.getStreet1(), address.getStreet2(), address.getStreet3(), address.getPostCode(), address.getPhoneNo(), address.getEmail()));
        }
        // ????????????????????????
        Map<String, DelOutboundDetail> detailMap = new HashMap<>(16);
        List<ShipmentDetailInfoDto> details;
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            details = new ArrayList<>();
            // ???????????????????????????
            /*for (DelOutboundDetail detail : detailList) {
                details.add(new ShipmentDetailInfoDto(detail.getProductName(), detail.getQty(), detail.getNewLabelCode()));
            }*/
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            details = new ArrayList<>();
            ShipmentDetailInfoDto shipmentDetailInfoDto = new ShipmentDetailInfoDto();
            shipmentDetailInfoDto.setSku(delOutbound.getNewSku());
            shipmentDetailInfoDto.setQty(delOutbound.getBoxNumber());
            details.add(shipmentDetailInfoDto);
        } else if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
            // ??????????????????
            List<DelOutboundCombinationVO> combinationVOList = this.delOutboundCombinationService.listByOrderNo(delOutbound.getOrderNo());
            // ??????sku??????
            List<String> skus = new ArrayList<>();
            for (DelOutboundCombinationVO combinationVO : combinationVOList) {
                skus.add(combinationVO.getSku());
            }
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", MessageUtil.to("??????SKU????????????", "Failed to query SKU information"));
            }
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            // ???????????????sku?????????????????????
            Map<String, ShipmentDetailInfoDto> shipmentDetailInfoDtoMap = new HashMap<>();
            for (DelOutboundCombinationVO combinationVO : combinationVOList) {
                String sku = combinationVO.getSku();
                if (shipmentDetailInfoDtoMap.containsKey(sku)) {
                    shipmentDetailInfoDtoMap.get(sku).addQty(combinationVO.getQty());
                } else {
                    shipmentDetailInfoDtoMap.put(sku, new ShipmentDetailInfoDto(sku, combinationVO.getQty(), ""));
                }
                // ??????sku????????????
                BaseProduct baseProduct = productMap.get(combinationVO.getSku());
                String bindCode = baseProduct.getBindCode();
                // ??????sku??????????????????
                if (StringUtils.isNotEmpty(bindCode)) {
                    // ?????????????????????????????????
                    if (shipmentDetailInfoDtoMap.containsKey(bindCode)) {
                        shipmentDetailInfoDtoMap.get(bindCode).addQty(combinationVO.getQty());
                    } else {
                        shipmentDetailInfoDtoMap.put(bindCode, new ShipmentDetailInfoDto(bindCode, combinationVO.getQty(), ""));
                    }
                }
            }
            details = new ArrayList<>(shipmentDetailInfoDtoMap.values());
            // ??????SKU????????????
            if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
                PackingRequirementInfoDto packingRequirementInfoDto = new PackingRequirementInfoDto();
                packingRequirementInfoDto.setQty(delOutbound.getBoxNumber());
                packingRequirementInfoDto.setDetails(details);
                createShipmentRequestDto.setPackingRequirement(packingRequirementInfoDto);
            }
        } else {
            // ??????sku????????????
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            // ???????????????sku?????????????????????
            Map<String, ShipmentDetailInfoDto> shipmentDetailInfoDtoMap = new HashMap<>();
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                if (shipmentDetailInfoDtoMap.containsKey(sku)) {
                    shipmentDetailInfoDtoMap.get(sku).addQty(detail.getQty());
                } else {
                    shipmentDetailInfoDtoMap.put(sku, new ShipmentDetailInfoDto(sku, detail.getQty(), detail.getNewLabelCode()));
                }
                // ??????sku????????????
                BaseProduct baseProduct = productMap.get(detail.getSku());
                String bindCode = baseProduct.getBindCode();
                // ??????sku??????????????????
                if (StringUtils.isNotEmpty(bindCode)) {
                    // ?????????????????????????????????
                    if (shipmentDetailInfoDtoMap.containsKey(bindCode)) {
                        shipmentDetailInfoDtoMap.get(bindCode).addQty(detail.getQty());
                    } else {
                        shipmentDetailInfoDtoMap.put(bindCode, new ShipmentDetailInfoDto(bindCode, detail.getQty(), detail.getNewLabelCode()));
                    }
                }
                // ??????sku???????????????????????????????????????
                detailMap.put(sku, detail);
            }
            details = new ArrayList<>(shipmentDetailInfoDtoMap.values());
        }
        // ??????SKU????????????
        if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
            // ?????????????????????????????????
            // details
            // packingRequirement.details
            List<ShipmentDetailInfoDto> detailInfoDtoList = new ArrayList<>();
            for (ShipmentDetailInfoDto detailInfoDto : details) {
                ShipmentDetailInfoDto infoDto = BeanMapperUtil.map(detailInfoDto, ShipmentDetailInfoDto.class);
                infoDto.setQty(infoDto.getQty() * delOutbound.getBoxNumber());
                detailInfoDtoList.add(infoDto);
            }
            createShipmentRequestDto.setDetails(detailInfoDtoList);
        } else {
            createShipmentRequestDto.setDetails(details);
        }
        createShipmentRequestDto.setIsPackingByRequired(delOutbound.getIsPackingByRequired());
        createShipmentRequestDto.setIsFirst(delOutbound.getIsFirst());
        createShipmentRequestDto.setNewSKU(delOutbound.getNewSku());
        // ????????????????????????
        TaskConfigInfo taskConfigInfo1 = TaskConfigInfoAdapter.getTaskConfigInfo(delOutbound.getOrderType());
        // ??????????????????
        if (null == taskConfigInfo1
                && StringUtils.isNotEmpty(delOutbound.getWarehouseCode())
                && StringUtils.isNotEmpty(delOutbound.getShipmentRule())) {
            PackageDeliveryConditions packageDeliveryConditions = new PackageDeliveryConditions();
            packageDeliveryConditions.setWarehouseCode(delOutbound.getWarehouseCode());
            packageDeliveryConditions.setProductCode(delOutbound.getShipmentRule());
            R<PackageDeliveryConditions> packageDeliveryConditionsR = this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions);
            PackageDeliveryConditions packageDeliveryConditionsRData = null;
            if (null != packageDeliveryConditionsR && Constants.SUCCESS == packageDeliveryConditionsR.getCode()) {
                packageDeliveryConditionsRData = packageDeliveryConditionsR.getData();
            }
            if (null != packageDeliveryConditionsRData) {
                TaskConfigInfo taskConfigInfo = new TaskConfigInfo();
                taskConfigInfo.setReceiveShippingType(packageDeliveryConditionsRData.getCommandNodeCode());
                taskConfigInfo.setIsPublishPackageMaterial("1".equals(packageDeliveryConditionsRData.getPackageReturned()));
                taskConfigInfo.setIsPublishPackageWeight("1".equals(packageDeliveryConditionsRData.getWeightReturned()));
                taskConfigInfo.setPrintShippingLabelType(packageDeliveryConditionsRData.getWarehouseLabelingCode());
                createShipmentRequestDto.setTaskConfig(taskConfigInfo);
                // ??????????????????
                delOutboundWrapperContext.setTaskConfigInfo(taskConfigInfo);
            }
            /*else {
                throw new CommonException("500", "??????????????????????????????????????????????????????" + delOutbound.getWarehouseCode() + "??????????????????" + delOutbound.getShipmentRule());
            }*/
        } else {
            createShipmentRequestDto.setTaskConfig(taskConfigInfo1);
            // ??????????????????
            delOutboundWrapperContext.setTaskConfigInfo(taskConfigInfo1);
        }
        // ??????????????????????????????
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            List<DelOutboundPackingVO> packingList = this.delOutboundPackingService.listByOrderNo(delOutbound.getOrderNo(), DelOutboundPackingTypeConstant.TYPE_1);
            if (CollectionUtils.isNotEmpty(packingList)) {
                PackingRequirementInfoDto packingRequirementInfoDto = new PackingRequirementInfoDto();
                packingRequirementInfoDto.setQty((long) packingList.size());
                List<DelOutboundPackingDetailVO> details1 = packingList.get(0).getDetails();
                if (CollectionUtils.isNotEmpty(details1)) {
                    List<ShipmentDetailInfoDto> shipmentDetailInfoDtos = new ArrayList<>();
                    for (DelOutboundPackingDetailVO delOutboundPackingDetailVO : details1) {
                        // sku?????????
                        ShipmentDetailInfoDto shipmentDetailInfoDto = new ShipmentDetailInfoDto();
                        shipmentDetailInfoDto.setSku(delOutboundPackingDetailVO.getSku());
                        shipmentDetailInfoDto.setQty(delOutboundPackingDetailVO.getQty());
                        // ????????????????????????????????????newLabelCode???????????????????????????SKU??????????????????
                        // shipmentDetailInfoDto.setNewLabelCode(delOutboundPackingDetailVO.getNewLabelCode());
                        // sku???????????????
                        DelOutboundDetail delOutboundDetail = detailMap.get(delOutboundPackingDetailVO.getSku());
                        ShipmentDetailInfoDto shipmentDetailInfoDtoBindCode = null;
                        if (null != delOutboundDetail) {
                            // ??????newLabelCode
                            shipmentDetailInfoDto.setNewLabelCode(delOutboundDetail.getNewLabelCode());
                            String bindCode = delOutboundDetail.getBindCode();
                            if (StringUtils.isNotEmpty(bindCode)) {
                                shipmentDetailInfoDtoBindCode = new ShipmentDetailInfoDto();
                                shipmentDetailInfoDtoBindCode.setSku(bindCode);
                                shipmentDetailInfoDtoBindCode.setQty(delOutboundPackingDetailVO.getQty());
                            }
                        }
                        // ??????sku??????
                        shipmentDetailInfoDtos.add(shipmentDetailInfoDto);
                        // ??????sku????????????
                        if (null != shipmentDetailInfoDtoBindCode) {
                            shipmentDetailInfoDtos.add(shipmentDetailInfoDtoBindCode);
                        }
                    }
                    packingRequirementInfoDto.setDetails(shipmentDetailInfoDtos);
                }
                createShipmentRequestDto.setPackingRequirement(packingRequirementInfoDto);
            }
        }
        CreateShipmentResponseVO createShipmentResponseVO = this.htpOutboundClientService.shipmentCreate(createShipmentRequestDto);
        if (null != createShipmentResponseVO) {
            if (null != createShipmentResponseVO.getSuccess()) {
                if (createShipmentResponseVO.getSuccess()) {
                    return createShipmentResponseVO.getOrderNo();
                } else {
                    String message = Utils.defaultValue(createShipmentResponseVO.getMessage(), MessageUtil.to("?????????????????????", "Failed to create the delivery order") + "2");
                    throw new CommonException("400", message);
                }
            }
            String message = Utils.defaultValue(createShipmentResponseVO.getErrors(), MessageUtil.to("?????????????????????", "Failed to create the delivery order") + "3");
            throw new CommonException("400", message);
        } else {
            throw new CommonException("400", MessageUtil.to("?????????????????????", "Failed to create the delivery order"));
        }
    }

    @Override
    public List<DelOutboundBringVerifyVO> bringVerifyByOrderNo(DelOutboundBringVerifyNoDto dto) {
        List<String> orderNos = dto.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", MessageUtil.to("????????????????????????", "The request parameter cannot be empty"));
        }
        LambdaQueryWrapper<DelOutbound> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DelOutbound::getOrderNo, orderNos);
        // ??????id??????????????????
        List<DelOutbound> delOutboundList = this.delOutboundService.list(queryWrapper);
        return this.bringVerifyProcess(delOutboundList);
    }

}
