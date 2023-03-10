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
        // 根据id查询出库信息
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        delOutboundList.forEach(delOutbound -> {
            ApplicationContext context = this.initContext(delOutbound);
            //创建出库单已经提审了
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
            throw new CommonException("400", MessageUtil.to("单据不存在", "The document does not exist"));
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
                logger.info(">>>>>[创建出库单{}]判断发货条件是否有效this.packageDeliveryConditionsFeignService.info(packageDeliveryConditions)"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());

                if(packageDeliveryConditionsR != null && packageDeliveryConditionsR.getCode() == 200){
                    if(packageDeliveryConditionsR.getData() == null || !"1".equals(packageDeliveryConditionsR.getData().getStatus())){
                        throw new CommonException("400", delOutbound.getShipmentRule()+ MessageUtil.to("物流服务未生效", "Logistics service is not effective:"+delOutbound.getOrderNo()));
                    }
                }

            }
            try {
                if (Objects.isNull(delOutbound)) {
                    throw new CommonException("400", MessageUtil.to("单据不存在", "The document does not exist"));
                }
                // 可以提审的状态：待提审，审核失败
                boolean isAuditFailed = DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState());
                if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || isAuditFailed)) {
                    throw new CommonException("400", MessageUtil.to("单据状态不正确，不能提审", "The document status is incorrect and cannot be submitted for approval"));
                }

                // 自提单判断面单是否上传
//                if(DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())){
//                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
//                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
//                basAttachmentQueryDTO.setAttachmentType(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getAttachmentType());
//                R<List<BasAttachment>> list = remoteAttachmentService.list(basAttachmentQueryDTO);
//                List<BasAttachment> dataAndException = R.getDataAndException(list);
//                if(dataAndException.size() == 0) {
//                    throw new CommonException("400", delOutbound.getOrderNo() + "单据面单未上传，不能提审");
//                }
//                }
                // 创建异步线程执行
                /*AsyncThreadObject asyncThreadObject = AsyncThreadObject.build();
                this.delOutboundBringVerifyAsyncService.bringVerifyAsync(delOutbound, asyncThreadObject);*/
                // 增加出库单已取消记录，异步处理，定时任务

                stopWatch.start();
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]创建出库临时表:"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());

                stopWatch.start();


                // 修改状态为提交中
                this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.REVIEWED_DOING);
                stopWatch.stop();
                logger.info(">>>>>[创建出库单{}]修改出库表为提审中:"+stopWatch.getLastTaskTimeMillis(), delOutbound.getOrderNo());
                resultList.add(new DelOutboundBringVerifyVO(delOutbound.getOrderNo(), true, MessageUtil.to("处理成功", "Processed successfully")));
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
                    resultList.add(new DelOutboundBringVerifyVO(MessageUtil.to("未知单号", "Unknown order No"), false, e.getMessage()));
                }
            }
        }
        return resultList;
    }
    @Override
    public List<DelOutboundBringVerifyVO> bringVerify(DelOutboundBringVerifyDto dto) {
        List<Long> ids = dto.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new CommonException("400", MessageUtil.to("请求参数不能为空", "The request parameter cannot be empty"));
        }
        // 根据id查询出库信息
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
        // 查询地址信息
        stopWatch.start();
        DelOutboundAddress address = this.delOutboundAddressService.getByOrderNo(orderNo);

        stopWatch.stop();
        logger.info(">>>>>[创建出库单{}查询地址] 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        if (null == address) {
            // 普通出口需要收货地址
            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())) {
                throw new CommonException("400", MessageUtil.to("收货地址信息不存在", "The receiving address information does not exist"));
            }
        }
        stopWatch.start();
        // 查询sku信息
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

            //汪总说：不存在取数据库名称
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
        logger.info(">>>>>[创建出库单{}查询sku信息] 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        stopWatch.start();

        // 查询仓库信息
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
        stopWatch.stop();

        logger.info(">>>>>[创建出库单{}查询仓库信息] 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

        if (null == warehouse) {
            throw new CommonException("400", MessageUtil.to("仓库信息不存在", "Warehouse information does not exist"));
        }
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = null;
        if (null != address) {
            stopWatch.start();
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
            stopWatch.stop();

            logger.info(">>>>>[创建出库单{}查询国家信息，收货地址所在的国家] 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", MessageUtil.to("国家信息不存在", "Country information does not exist"));
            }
        }
        // 查询sku信息
        List<BaseProduct> productList = null;
        if (CollectionUtils.isNotEmpty(detailList)) {
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            List<String> skus = new ArrayList<>();
            for (DelOutboundDetail detail : detailList) {
                skus.add(detail.getSku());
            }
            // conditionQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
            conditionQueryDto.setSkus(skus);
            // 转运出库的不查询sku明细信息
            if (!DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
                stopWatch.start();
                productList = this.baseProductClientService.queryProductList(conditionQueryDto);
                stopWatch.stop();

                logger.info(">>>>>[创建出库单{}转运出库的不查询sku明细信息] 耗时{}", delOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());

                if (CollectionUtils.isEmpty(productList)) {
                    throw new CommonException("400", MessageUtil.to("查询SKU信息失败", "Failed to query SKU information"));
                }
            }
        }
        // 查询sku包材信息
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
        // 查询地址信息
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // 查询sku信息
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // 查询仓库信息
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // 查询sku信息
        List<BaseProduct> productList = delOutboundWrapperContext.getProductList();

        Integer  thridPartStatus = delOutbound.getThridPartStatus();

        logger.info("{},pricing,thridPartStatus:{}",delOutbound.getOrderNo(),thridPartStatus);

        if(thridPartStatus.equals(1)){
            delOutbound.setLength(1D);
            delOutbound.setWidth(1D);
            delOutbound.setHeight(1D);
        }

        logger.info("{},修改规格后 :{}",delOutbound.getOrderNo(),delOutbound.getLength());

        // 包裹信息
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
                // 查询包材的信息
                Set<String> skus = new HashSet<>();
                for (DelOutboundDetail detail : detailList) {
                    // sku包材信息
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
                    logger.info(">>>>>[创建出库单{}]-Pricing计算返回结果：baseProductClientService.queryProductList 耗时{}", e-s);

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
                        throw new CommonException("400", MessageUtil.to("查询SKU[" + sku + "]信息失败",
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
                    // 判断有没有包材
                    String bindCode = detail.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode)) {
                        BaseProduct baseProduct = bindCodeMap.get(bindCode);
                        if (null == baseProduct) {
                            throw new CommonException("400",  MessageUtil.to(
                                    "查询SKU[" + sku + "]的包材[" + bindCode + "]信息失败",
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
                // 批量出口传WMS返回的装箱明细过去计算
                if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {

                    Long s = System.currentTimeMillis();
                    // 查询装箱明细
                    List<DelOutboundPacking> packingList = this.delOutboundPackingService.packageListByOrderNo(delOutbound.getOrderNo(), DelOutboundPackingTypeConstant.TYPE_2);

                    Long e = System.currentTimeMillis();
                    logger.info(">>>>>[创建出库单{}]-Pricing计算返回结果：delOutboundPackingService.packageListByOrderNo 耗时{}", e-s);

                    if (CollectionUtils.isEmpty(packingList)) {
                        throw new CommonException("400", MessageUtil.to("没有查询到WMS返回的装箱信息",
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
                            throw new CommonException("400", MessageUtil.to("查询SKU[" + sku + "]信息失败", "Failed to query SKU ["+sku+"] information"));
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
            logger.info(">>>>>[创建出库单{}]-Pricing计算返回结果：chargeFeignService.selectBasProductService 耗时{}", e-s);
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

        // 计算包裹费用
        CalcShipmentFeeCommand calcShipmentFeeCommand = new CalcShipmentFeeCommand();
        // true代表需要验证，false的话，主要是用于测算
        calcShipmentFeeCommand.setAddressValifition(true);
        // 产品代码就是选择的物流承运商
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
            // 收货地址
            calcShipmentFeeCommand.setToAddress(fromAddress);
            // 发货地址
            calcShipmentFeeCommand.setFromAddress(toAddress);
        }else {
            // 收货地址
            calcShipmentFeeCommand.setToAddress(toAddress);
            // 发货地址
            calcShipmentFeeCommand.setFromAddress(fromAddress);
        }
        // 联系信息
        calcShipmentFeeCommand.setToContactInfo(new ContactInfo(address.getConsignee(), address.getPhoneNo(), address.getEmail(), null));
        // calcShipmentFeeCommand.setCalcTimeForDiscount(new Date());
        Long s= System.currentTimeMillis();
        // 调用接口
        ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> responseObjectWrapper = this.htpPricedProductClientService.pricing(calcShipmentFeeCommand);
        Long e = System.currentTimeMillis();
        logger.info(">>>>>[创建出库单{}]-Pricing计算返回结果：htpPricedProductClientService.pricing 耗时{}", e-s);

        return responseObjectWrapper;
    }

    @Override
    public ShipmentOrderResult shipmentOrder(DelOutboundWrapperContext delOutboundWrapperContext) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        String orderNo = delOutbound.getOrderNo();
        String shipmentService = delOutbound.getShipmentService();
       /* if (StringUtils.isEmpty(shipmentService)) {
            throw new CommonException("400", MessageUtil.to("发货服务名称为空", "The delivery service name is empty"));
        }*/
        // 查询地址信息
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // 查询sku信息
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // 查询仓库信息
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // 创建承运商物流订单
        CreateShipmentOrderCommand createShipmentOrderCommand = new CreateShipmentOrderCommand();
        createShipmentOrderCommand.setWarehouseCode(delOutbound.getWarehouseCode());
        String referenceNumber = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        // 改成uuid
        createShipmentOrderCommand.setReferenceNumber(referenceNumber);
        // 订单号传refno
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
            logger.info(">>>>>[创建出库单{}]创建承运商 chargeFeignService.selectBasProductService 耗时{}", e-s);

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
        // 税号信息
        String ioss = delOutbound.getIoss();
        if (StringUtils.isNotEmpty(ioss)) {
            List<Taxation> taxations = new ArrayList<>();
            taxations.add(new Taxation("IOSS", ioss));
            createShipmentOrderCommand.setTaxations(taxations);
        }
        createShipmentOrderCommand.setCodAmount(delOutbound.getCodAmount());
        // 包裹信息
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
            logger.info(">>>>>[创建出库单{}]创建承运商 baseProductClientService.queryProductList 耗时{}", e-s);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", MessageUtil.to("查询SKU[" + delOutbound.getNewSku() + "]信息失败",
                        "Failed to query SKU ["+delOutbound. getNewSku()+"] information"));
            }
            BaseProduct product = productList.get(0);
            packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), weightInGram = Utils.valueOfDouble(product.getWeight()),
                    new Size(product.getLength(), product.getWidth(), product.getHeight()),
                    Utils.valueOfLong(delOutbound.getBoxNumber()), product.getHsCode(), String.valueOf(delOutbound.getId()), delOutbound.getNewSku()));
        } else if(DelOutboundOrderTypeEnum.MULTIBOX.getCode().equals(delOutbound.getOrderType())) {

            // 查询sku信息
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("查询SKU[" + sku + "]信息失败",
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
            // 查询sku信息
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("查询SKU[" + sku + "]信息失败",
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
        logger.info(">>>>>[创建出库单{}]创建承运商 htpCarrierClientService.shipmentOrder 耗时{}", e-s);
        if (null == responseObjectWrapper) {
            throw new CommonException("400", MessageUtil.to("创建承运商物流订单失败，调用承运商系统无响应",
                    "Failed to create carrier logistics order, calling carrier system has no response"));
        }
        if (responseObjectWrapper.isSuccess()) {
            // 保存挂号
            // 判断结果集是不是正确的
            ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
            if (null == shipmentOrderResult) {
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(),
                        shipmentService, null, (MessageUtil.to("创建承运商物流订单失败，调用承运商系统返回数据为空",
                        "Failed to create the carrier logistics order. The data returned by calling the carrier system is blank")));
                throw new CommonException("400", MessageUtil.to("创建承运商物流订单失败，调用承运商系统返回数据为空",
                        "Failed to create the carrier logistics order. The data returned by calling the carrier system is blank"));
            }
            if (null == shipmentOrderResult.getSuccess() || !shipmentOrderResult.getSuccess()) {
                // 判断有没有错误信息
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
                    builder.append(MessageUtil.to("创建承运商物流订单失败，调用承运商系统失败，返回错误信息为空",
                            "Failed to create the carrier logistics order, failed to call the carrier system, and the returned error message is blank"));
                }
                if(StringUtils.contains(builder.toString(), "提交失败!【订单号"+delOutbound.getOrderNo()+"重复】")){
                    logger.info("创建承运商物流订单{}第三方失败1exceptionMessage，{}", delOutbound.getOrderNo(), builder.toString());
                    this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, delOutbound.getTrackingNo(), null);
                }else{
                    logger.info("创建承运商物流订单{}第三方失败2exceptionMessage，{}", delOutbound.getOrderNo(), builder.toString());
                    this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, null, builder.toString());
                    throw new CommonException("412", builder.toString());
                }
            }
            this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, shipmentOrderResult.getMainTrackingNumber(), null);
            shipmentOrderResult.setReferenceNumber(referenceNumber);
            return shipmentOrderResult;
        } else {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError(), true), "创建承运商物流订单失败，调用承运商系统失败");
            logger.info("创建承运商物流订单{}接口失败exceptionMessage，{}", delOutbound.getOrderNo(), exceptionMessage);
            if(StringUtils.contains(exceptionMessage, "提交失败!【订单号"+delOutbound.getOrderNo()+"重复】")){
                //发货指令会因为供应商不允许创建重复订单的原因
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, delOutbound.getTrackingNo(), exceptionMessage);
                return null;
            }else{
                this.transferCallback(delOutbound.getOrderNo(), delOutbound.getShopifyOrderNo(), shipmentService, null, exceptionMessage);
                if("[408]请求超时".equals(exceptionMessage)){
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
            logger.info(">>>>>[创建出库单{}]转仓库回调耗时{}", orderNo, stopWatch.getLastTaskInfo().getTimeMillis());
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
            throw new CommonException("400", MessageUtil.to("发货服务名称为空", "The delivery service name is empty"));
        }*/
        // 查询地址信息
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // 查询sku信息
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // 查询仓库信息
        BasWarehouse warehouse = delOutboundWrapperContext.getWarehouse();
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // 创建承运商物流订单
        CreateShipmentOrderCommand createShipmentOrderCommand = new CreateShipmentOrderCommand();
//        createShipmentOrderCommand.setAmazonLogisticsRouteId(delOutbound.getAmazonLogisticsRouteId());
        createShipmentOrderCommand.setWarehouseCode(delOutbound.getWarehouseCode());
        // 改成uuid
        createShipmentOrderCommand.setReferenceNumber(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        // 订单号传refno
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
        // 税号信息
        String ioss = delOutbound.getIoss();
        if (StringUtils.isNotEmpty(ioss)) {
            List<Taxation> taxations = new ArrayList<>();
            taxations.add(new Taxation("IOSS", ioss));
            createShipmentOrderCommand.setTaxations(taxations);
        }
        createShipmentOrderCommand.setCodAmount(delOutbound.getCodAmount());
        // 包裹信息
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
                throw new CommonException("400", MessageUtil.to("查询SKU[" + delOutbound.getNewSku() + "]信息失败",
                        "Failed to query SKU ["+delOutbound. getNewSku()+"] information"));            }
            BaseProduct product = productList.get(0);
            packageItems.add(new PackageItem(product.getProductName(), product.getProductNameChinese(), product.getDeclaredValue(), weightInGram = Utils.valueOfDouble(product.getWeight()),
                    new Size(product.getLength(), product.getWidth(), product.getHeight()),
                    Utils.valueOfLong(delOutbound.getBoxNumber()), product.getHsCode(), String.valueOf(delOutbound.getId()), delOutbound.getNewSku()));
        } else {
            // 查询sku信息
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("400",  MessageUtil.to("查询SKU[" + sku + "]信息失败",
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
//            throw new CommonException("400", MessageUtil.to("包裹重量为0或者小于0，不能创建承运商物流订单",
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
            throw new CommonException("400",MessageUtil.to("创建亚马逊承运商物流订单失败，调用承运商系统无响应",
                    "Failed to create Amazon carrier logistics order, calling carrier system has no response"

            ) );
        }
        if (!responseObjectWrapper.isSuccess()) {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError(), true), "创建承运商物流订单失败，调用承运商系统失败");
            if("[408]请求超时".equals(exceptionMessage)){
                throw new CommonException("408", exceptionMessage);
            }
            throw new CommonException("400", exceptionMessage);
        }
        // 保存挂号
        // 判断结果集是不是正确的
        ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
        if (null == shipmentOrderResult) {
            throw new CommonException("400", MessageUtil.to("创建亚马逊承运商物流订单失败，调用承运商系统返回数据为空",
                    "Failed to create an Amazon carrier logistics order. The data returned by calling the carrier system is blank"));
        }
        if (null == shipmentOrderResult.getSuccess() || !shipmentOrderResult.getSuccess()) {
            // 判断有没有错误信息
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
                builder.append(MessageUtil.to("创建亚马逊承运商物流订单失败，调用承运商系统失败，返回错误信息为空", "Failed to create an Amazon carrier logistics order, failed to call the carrier system, and the error returned is blank"));
            }
            throw new CommonException("400", builder.toString());
        }
        return shipmentOrderResult;
    }

    @Override
    public void shipmentRule(DelOutbound delOutbound) {
        String logMessage;
        // 判断PRC算费返回的发货规则是否为空，如果是空就那承运商物流编码赋值
        String shipmentRule = delOutbound.getProductShipmentRule();
        if (StringUtils.isEmpty(shipmentRule)) {
            shipmentRule = delOutbound.getShipmentRule();
        }
        // 销毁出库单的发货规则默认XiaoHui
        if (DelOutboundOrderTypeEnum.DESTROY.getCode().equals(shipmentRule)) {
            shipmentRule = "XiaoHui";
        }
        out:
        if (StringUtils.isEmpty(shipmentRule)) {
            logMessage = MessageUtil.to("发货规则为空", "Shipment rule is empty");
        } else {
            // 发货规则唯一MD5值
            String shipmentRuleMd5 = MD5.create().digestHex(shipmentRule);
            String key = "Delivery:ShipmentRule:" + shipmentRuleMd5;
            Object o = this.redisTemplate.opsForValue().get(key);
            if (Objects.nonNull(o)) {
                logMessage = MessageUtil.to("发货规则已存在不更新WMS", "Shipment rule already exists Do not update WMS");
                break out;
            }
            logMessage = MessageUtil.to("发货规则不存在，更新WMS", "Shipment rule does not exist, update WMS");
            // 调用新增/修改发货规则
            AddShipmentRuleRequest addShipmentRuleRequest = new AddShipmentRuleRequest();
            addShipmentRuleRequest.setWarehouseCode(delOutbound.getWarehouseCode());
            addShipmentRuleRequest.setOrderNo(delOutbound.getOrderNo());
            String orderType = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getOrderType(), "");
            String shipmentChannel = com.szmsd.common.core.utils.StringUtils.nvl(delOutbound.getShipmentChannel(), "");
            if (orderType.equals(DelOutboundOrderTypeEnum.BATCH.getCode()) && "SelfPick".equals(shipmentChannel)) {
                addShipmentRuleRequest.setShipmentRule(delOutbound.getDeliveryAgent());
                addShipmentRuleRequest.setGetLabelType(DelOutboundTrackingAcquireTypeEnum.NONE.getCode());
            } else {
                // 获取PRC计费之后返回的发货规则
                addShipmentRuleRequest.setShipmentRule(shipmentRule);
                addShipmentRuleRequest.setGetLabelType(delOutbound.getTrackingAcquireType());
            }
            IHtpIBasClientService htpIBasClientService = SpringUtils.getBean(IHtpIBasClientService.class);
            BaseOperationResponse baseOperationResponse = htpIBasClientService.shipmentRule(addShipmentRuleRequest);
            if (null == baseOperationResponse) {
                throw new CommonException("400", MessageUtil.to("新增/修改发货规则失败", "Failed to add/modify the shipment rule"));
            }
            if (null == baseOperationResponse.getSuccess()) {
                baseOperationResponse.setSuccess(false);
            }
            if (!baseOperationResponse.getSuccess()) {
                String msg = baseOperationResponse.getMessage();
                if (com.szmsd.common.core.utils.StringUtils.isEmpty(msg)) {
                    msg = baseOperationResponse.getErrors();
                }
                String message = Utils.defaultValue(msg, MessageUtil.to("新增/修改发货规则失败", "Failed to add/modify the shipment rule"));
                throw new CommonException("400", message);
            }
            // 请求成功，添加到redis中，下次判断redis里面存在就不推送给WMS
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
                // 内部异常，不再重试，直接抛出去
                throw new CommonException("500", MessageUtil.to("创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage()

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
                    logger.error("复制文件失败，" + e.getMessage(), e);
                }
            }
            try {
                FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                return path;
            } catch (IOException e) {
                // 内部异常，不再重试，直接抛出去
                throw new CommonException("500", MessageUtil.to("保存标签文件失败，Error：" + e.getMessage(),
                        "Failed to save label file, Error:"+e.getMessage())
                        );
            }
        }
        logger.error("保存标签文件流失败");
        throw new CommonException("500", MessageUtil.to("获取标签文件流失败", "Failed to get tag file stream"));

    }
    @Override
    public String getShipmentLabel(DelOutbound delOutbound) {
        if (null == delOutbound) {
            throw new CommonException("500",  MessageUtil.to("出库单信息不能为空", "Delivery list information cannot be empty"));
        }
        String orderNumber = delOutbound.getShipmentOrderNumber();
        if (StringUtils.isEmpty(orderNumber)) {
            return null;
        }
        // 获取标签
        CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
        command.setWarehouseCode(delOutbound.getWarehouseCode());
        command.setOrderNumber(orderNumber);
        command.setShipmentOrderLabelUrl(delOutbound.getShipmentOrderLabelUrl());
        DelOutboundOperationLogEnum.SMT_LABEL.listener(delOutbound);
        logger.info("正在获取标签文件，单号：{}，承运商订单号：{}", delOutbound.getOrderNo(), orderNumber);
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
                        // 内部异常，不再重试，直接抛出去
                        throw new CommonException("500", MessageUtil.to("创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage()

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
                            logger.error("复制文件失败，" + e.getMessage(), e);
                        }
                    }
                    try {
                        FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                        return path;
                    } catch (IOException e) {
                        // 内部异常，不再重试，直接抛出去
                        throw new CommonException("500", MessageUtil.to("保存标签文件失败，Error：" + e.getMessage(),
                                "Failed to save label file, Error:"+e.getMessage())
                        );                    }
                }
            } else {
                // 接口响应异常，继续重试
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()),

                        MessageUtil.to("获取标签文件流失败2", "Failed to get tag file stream2"));
                logger.error(exceptionMessage);
                throw new CommonException("500", exceptionMessage);
            }
        } else {
            // 接口响应异常继续重试
            logger.error("获取标签文件流失败");
            throw new CommonException("500", MessageUtil.to("获取标签文件流失败", "Failed to get tag file stream"));
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
            //生成下自提标签文件，如果有就不生成
            delOutboundService.labelSelfPick(null, dto);
            selfPickLabelFilePath = DelOutboundServiceImplUtil.getSelfPickLabelFilePath(delOutbound) + "/" + delOutbound.getOrderNo() + ".pdf";
        }



        String pathname = null;
        // 如果是批量出库，将批量出库上传的文件和标签文件合并在一起传过去
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            // 判断文件是否已经创建
            String mergeFileDirPath = DelOutboundServiceImplUtil.getBatchMergeFilePath(delOutbound);
            File mergeFileDir = new File(mergeFileDirPath);
            if (!mergeFileDir.exists()) {
                try {
                    FileUtils.forceMkdir(mergeFileDir);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("创建文件夹失败，", "Failed to create folder,") + e.getMessage());
                }
            }
            String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
            File mergeFile = new File(mergeFilePath);
            if (!mergeFile.exists()) {
                String boxFilePath = "";
                // 标签文件
                if (delOutbound.getIsLabelBox()) {
                    BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                    basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                    basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                    R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                    if (null != listR && null != listR.getData()) {
                        List<BasAttachment> attachmentList = listR.getData();
                        if (CollectionUtils.isNotEmpty(attachmentList)) {
                            BasAttachment attachment = attachmentList.get(0);
                            // 箱标文件 - 上传的
                            boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                        }
                    }
                }
                String labelFilePath = "";
                if ("SelfPick".equals(delOutbound.getShipmentChannel())) {
                    // 批量出库的自提出库标签是上传的
                    // 查询上传的文件
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
                        throw new CommonException("500", MessageUtil.to("箱标文件未上传", "The box label file is not uploaded"));
                    }
                } else {
                    // 标签文件 - 从承运商物流那边获取的
                    labelFilePath = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";
                }
                String uploadBoxLabel = null;
                if("Y".equals(delOutbound.getUploadBoxLabel())){
                    uploadBoxLabel = getBoxLabel(delOutbound);
                }
                // 合并文件
                try {
                    if (PdfUtil.merge(mergeFilePath, boxFilePath, labelFilePath, uploadBoxLabel, selfPickLabelFilePath)) {
                        pathname = mergeFilePath;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", MessageUtil.to("合并箱标文件，标签文件失败","Merging box label file, label file failed"));
                }
            }else{
                pathname = mergeFilePath;
            }
        }
        if (null == pathname) {

            String boxFilePath = "";
            // 标签文件
            if (delOutbound.getIsLabelBox()) {
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                        BasAttachment attachment = attachmentList.get(0);
                        // 箱标文件 - 上传的
                        boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                    }
                }
            }else{
                //暴力写死
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_DOCUMENT.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                        BasAttachment attachment = attachmentList.get(0);
                        // 箱标文件 - 上传的
                        boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                    }
                }
            }

            // 判断从承运商获取的标签文件是否存在
            pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber() + ".pdf";


            //此变量表示通过导入箱标的附件
            String uploadBoxLabel = null;
            if("Y".equals(delOutbound.getUploadBoxLabel())){
                uploadBoxLabel = getBoxLabel(delOutbound);
            }
            logger.info("更新出库单{}标签,文件{},自提标签{},箱标{}",delOutbound.getOrderNo(), pathname, selfPickLabelFilePath, uploadBoxLabel);
            pathname = this.mergeFile(delOutbound, pathname, boxFilePath, selfPickLabelFilePath, uploadBoxLabel);

        }
        File labelFile = new File(pathname);
        // 判断标签文件是否存在
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
                    throw new CommonException("400", MessageUtil.to("更新标签失败，请求无响应", "Failed to update the label. The request is unresponsive"));
                }
                if (!responseVO.getSuccess()) {
                    throw new CommonException("400", MessageUtil.to("更新标签失败，", "Failed to update label,") + Utils.defaultValue(responseVO.getMessage(), ""));
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new CommonException("500", "读取标签文件失败");
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
                throw new CommonException("500", MessageUtil.to("创建文件夹失败，", "Failed to create folder,") + e.getMessage());
            }
        }
        String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
        new File(mergeFilePath).deleteOnExit();
        logger.info("{}出库单传wms数据集合{}", delOutbound.getOrderNo(), urls);
            ;
        try {
            PdfUtil.merge(mergeFilePath, urls);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new CommonException("500", MessageUtil.to("合并箱标文件，标签文件失败","Merging box label file, label file failed"));
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
            throw new CommonException("400", MessageUtil.to("更新发货指令失败，请求无响应", "Failed to update the delivery instruction. The request has no response"));
        }
        if (!responseVO.getSuccess()) {
            throw new CommonException("400", MessageUtil.to("更新发货指令失败，", "Failed to update the delivery instruction") + Utils.defaultValue(responseVO.getMessage(), ""));
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
        logger.info("单号{}，shipmentShipping异常信息{}", delOutbound.getOrderNo(), exRemark);
        shipmentUpdateRequestDto.setExRemark(Utils.defaultValue(exRemark, MessageUtil.to("操作失败", "operation failed")));
        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
        htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
    }

    @Async
    @Override
    public void ignoreExceptionInfo(String orderNo) {
        try {
            int ignore = this.exceptionInfoClientService.ignore(orderNo);
            logger.info("出库单[{}]调用忽略异常信息成功，ignore:{}", orderNo, ignore);
        } catch (Exception e) {
            logger.error("出库单[" + orderNo + "]调用忽略异常信息接口失败，" + e.getMessage(), e);
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
            throw new CommonException("400", MessageUtil.to("取消承运商物流订单失败", "Failed to cancel the carrier logistics order"));
        }
        CancelShipmentOrderBatchResult cancelShipmentOrderBatchResult = responseObject.getObject();
        List<CancelShipmentOrderResult> cancelOrders = cancelShipmentOrderBatchResult.getCancelOrders();
        for (CancelShipmentOrderResult cancelOrder : cancelOrders) {
            if (!cancelOrder.isSuccess()) {
                throw new CommonException("400", MessageUtil.to("取消承运商物流订单失败", "Failed to cancel the carrier logistics order")+"2");
            }
        }
    }

    @Override
    public String shipmentCreate(DelOutboundWrapperContext delOutboundWrapperContext, String trackingNo) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
        // 查询地址信息
        DelOutboundAddress address = delOutboundWrapperContext.getAddress();
        // 查询sku信息
        List<DelOutboundDetail> detailList = delOutboundWrapperContext.getDetailList();
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = delOutboundWrapperContext.getCountry();
        // 推单到WMS
        CreateShipmentRequestDto createShipmentRequestDto = new CreateShipmentRequestDto();
        createShipmentRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
        createShipmentRequestDto.setOrderType(delOutbound.getOrderType());
        createShipmentRequestDto.setSellerCode(delOutbound.getSellerCode());
        createShipmentRequestDto.setTrackingNo(trackingNo);
        createShipmentRequestDto.setRefCode(delOutbound.getRefNo());
        // 获取从prc返回的发货规则
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
        // 销毁出库
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
        // 如果是批量出库，出库渠道是自提，不传地址信息
        if (null != address && !isBatchSelfPick) {
            createShipmentRequestDto.setAddress(new ShipmentAddressDto(address.getConsignee(),
                    address.getCountryCode(), country.getName(), address.getZone(), address.getStateOrProvince(), address.getCity(),
                    address.getStreet1(), address.getStreet2(), address.getStreet3(), address.getPostCode(), address.getPhoneNo(), address.getEmail()));
        }
        // 转运出库明细处理
        Map<String, DelOutboundDetail> detailMap = new HashMap<>(16);
        List<ShipmentDetailInfoDto> details;
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            details = new ArrayList<>();
            // 转运出库，明细不传
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
            // 查询组合信息
            List<DelOutboundCombinationVO> combinationVOList = this.delOutboundCombinationService.listByOrderNo(delOutbound.getOrderNo());
            // 查询sku信息
            List<String> skus = new ArrayList<>();
            for (DelOutboundCombinationVO combinationVO : combinationVOList) {
                skus.add(combinationVO.getSku());
            }
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", MessageUtil.to("查询SKU信息失败", "Failed to query SKU information"));
            }
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            // 处理包材或sku明细重复的问题
            Map<String, ShipmentDetailInfoDto> shipmentDetailInfoDtoMap = new HashMap<>();
            for (DelOutboundCombinationVO combinationVO : combinationVOList) {
                String sku = combinationVO.getSku();
                if (shipmentDetailInfoDtoMap.containsKey(sku)) {
                    shipmentDetailInfoDtoMap.get(sku).addQty(combinationVO.getQty());
                } else {
                    shipmentDetailInfoDtoMap.put(sku, new ShipmentDetailInfoDto(sku, combinationVO.getQty(), ""));
                }
                // 获取sku详细信息
                BaseProduct baseProduct = productMap.get(combinationVO.getSku());
                String bindCode = baseProduct.getBindCode();
                // 判断sku是否存在包材
                if (StringUtils.isNotEmpty(bindCode)) {
                    // 存在包材，增加包材信息
                    if (shipmentDetailInfoDtoMap.containsKey(bindCode)) {
                        shipmentDetailInfoDtoMap.get(bindCode).addQty(combinationVO.getQty());
                    } else {
                        shipmentDetailInfoDtoMap.put(bindCode, new ShipmentDetailInfoDto(bindCode, combinationVO.getQty(), ""));
                    }
                }
            }
            details = new ArrayList<>(shipmentDetailInfoDtoMap.values());
            // 组合SKU特殊处理
            if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
                PackingRequirementInfoDto packingRequirementInfoDto = new PackingRequirementInfoDto();
                packingRequirementInfoDto.setQty(delOutbound.getBoxNumber());
                packingRequirementInfoDto.setDetails(details);
                createShipmentRequestDto.setPackingRequirement(packingRequirementInfoDto);
            }
        } else {
            // 查询sku详细信息
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            // 处理包材或sku明细重复的问题
            Map<String, ShipmentDetailInfoDto> shipmentDetailInfoDtoMap = new HashMap<>();
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                if (shipmentDetailInfoDtoMap.containsKey(sku)) {
                    shipmentDetailInfoDtoMap.get(sku).addQty(detail.getQty());
                } else {
                    shipmentDetailInfoDtoMap.put(sku, new ShipmentDetailInfoDto(sku, detail.getQty(), detail.getNewLabelCode()));
                }
                // 获取sku详细信息
                BaseProduct baseProduct = productMap.get(detail.getSku());
                String bindCode = baseProduct.getBindCode();
                // 判断sku是否存在包材
                if (StringUtils.isNotEmpty(bindCode)) {
                    // 存在包材，增加包材信息
                    if (shipmentDetailInfoDtoMap.containsKey(bindCode)) {
                        shipmentDetailInfoDtoMap.get(bindCode).addQty(detail.getQty());
                    } else {
                        shipmentDetailInfoDtoMap.put(bindCode, new ShipmentDetailInfoDto(bindCode, detail.getQty(), detail.getNewLabelCode()));
                    }
                }
                // 记录sku的信息，在批量里面需要使用
                detailMap.put(sku, detail);
            }
            details = new ArrayList<>(shipmentDetailInfoDtoMap.values());
        }
        // 组合SKU特殊处理
        if (DelOutboundOrderTypeEnum.NEW_SKU.getCode().equals(delOutbound.getOrderType())) {
            // 特殊处理这两个字段的值
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
        // 获取发货计划条件
        TaskConfigInfo taskConfigInfo1 = TaskConfigInfoAdapter.getTaskConfigInfo(delOutbound.getOrderType());
        // 查询发货条件
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
                // 上下文值传递
                delOutboundWrapperContext.setTaskConfigInfo(taskConfigInfo);
            }
            /*else {
                throw new CommonException("500", "产品服务未配置，请联系管理员。仓库：" + delOutbound.getWarehouseCode() + "，产品代码：" + delOutbound.getShipmentRule());
            }*/
        } else {
            createShipmentRequestDto.setTaskConfig(taskConfigInfo1);
            // 上下文值传递
            delOutboundWrapperContext.setTaskConfigInfo(taskConfigInfo1);
        }
        // 批量出口增加装箱要求
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
            List<DelOutboundPackingVO> packingList = this.delOutboundPackingService.listByOrderNo(delOutbound.getOrderNo(), DelOutboundPackingTypeConstant.TYPE_1);
            if (CollectionUtils.isNotEmpty(packingList)) {
                PackingRequirementInfoDto packingRequirementInfoDto = new PackingRequirementInfoDto();
                packingRequirementInfoDto.setQty((long) packingList.size());
                List<DelOutboundPackingDetailVO> details1 = packingList.get(0).getDetails();
                if (CollectionUtils.isNotEmpty(details1)) {
                    List<ShipmentDetailInfoDto> shipmentDetailInfoDtos = new ArrayList<>();
                    for (DelOutboundPackingDetailVO delOutboundPackingDetailVO : details1) {
                        // sku的信息
                        ShipmentDetailInfoDto shipmentDetailInfoDto = new ShipmentDetailInfoDto();
                        shipmentDetailInfoDto.setSku(delOutboundPackingDetailVO.getSku());
                        shipmentDetailInfoDto.setQty(delOutboundPackingDetailVO.getQty());
                        // 保存的时候装箱信息里面的newLabelCode没有保存值，需要从SKU明细里面获取
                        // shipmentDetailInfoDto.setNewLabelCode(delOutboundPackingDetailVO.getNewLabelCode());
                        // sku包材的信息
                        DelOutboundDetail delOutboundDetail = detailMap.get(delOutboundPackingDetailVO.getSku());
                        ShipmentDetailInfoDto shipmentDetailInfoDtoBindCode = null;
                        if (null != delOutboundDetail) {
                            // 设置newLabelCode
                            shipmentDetailInfoDto.setNewLabelCode(delOutboundDetail.getNewLabelCode());
                            String bindCode = delOutboundDetail.getBindCode();
                            if (StringUtils.isNotEmpty(bindCode)) {
                                shipmentDetailInfoDtoBindCode = new ShipmentDetailInfoDto();
                                shipmentDetailInfoDtoBindCode.setSku(bindCode);
                                shipmentDetailInfoDtoBindCode.setQty(delOutboundPackingDetailVO.getQty());
                            }
                        }
                        // 添加sku信息
                        shipmentDetailInfoDtos.add(shipmentDetailInfoDto);
                        // 添加sku包材信息
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
                    String message = Utils.defaultValue(createShipmentResponseVO.getMessage(), MessageUtil.to("创建出库单失败", "Failed to create the delivery order") + "2");
                    throw new CommonException("400", message);
                }
            }
            String message = Utils.defaultValue(createShipmentResponseVO.getErrors(), MessageUtil.to("创建出库单失败", "Failed to create the delivery order") + "3");
            throw new CommonException("400", message);
        } else {
            throw new CommonException("400", MessageUtil.to("创建出库单失败", "Failed to create the delivery order"));
        }
    }

    @Override
    public List<DelOutboundBringVerifyVO> bringVerifyByOrderNo(DelOutboundBringVerifyNoDto dto) {
        List<String> orderNos = dto.getOrderNos();
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("400", MessageUtil.to("请求参数不能为空", "The request parameter cannot be empty"));
        }
        LambdaQueryWrapper<DelOutbound> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(DelOutbound::getOrderNo, orderNos);
        // 根据id查询出库信息
        List<DelOutbound> delOutboundList = this.delOutboundService.list(queryWrapper);
        return this.bringVerifyProcess(delOutboundList);
    }

}
