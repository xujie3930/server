package com.szmsd.delivery.service.wrapper.impl;

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
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundPackingTypeConstant;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
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
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Package;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
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
    @Autowired
    private IDelOutboundBringVerifyAsyncService delOutboundBringVerifyAsyncService;
    @Autowired
    private RemoteAttachmentService remoteAttachmentService;

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

    @Override
    public List<DelOutboundBringVerifyVO> bringVerify(DelOutboundBringVerifyDto dto) {
        List<Long> ids = dto.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            throw new CommonException("400", "请求参数不能为空");
        }
        // 根据id查询出库信息
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        if (CollectionUtils.isEmpty(delOutboundList)) {
            throw new CommonException("400", "单据不存在");
        }
        List<DelOutboundBringVerifyVO> resultList = new ArrayList<>();
        for (DelOutbound delOutbound : delOutboundList) {
            try {
                if (Objects.isNull(delOutbound)) {
                    throw new CommonException("400", "单据不存在");
                }
                // 可以提审的状态：待提审，审核失败
                boolean isAuditFailed = DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState());
                if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || isAuditFailed)) {
                    throw new CommonException("400", "单据状态不正确，不能提审");
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
                AsyncThreadObject asyncThreadObject = AsyncThreadObject.build();
                this.delOutboundBringVerifyAsyncService.bringVerifyAsync(delOutbound, asyncThreadObject);
                // 修改状态为提交中
                this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.REVIEWED_DOING);
                resultList.add(new DelOutboundBringVerifyVO(delOutbound.getOrderNo(), true, "处理成功"));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                if (null != delOutbound) {
                    resultList.add(new DelOutboundBringVerifyVO(delOutbound.getOrderNo(), false, e.getMessage()));
                } else {
                    resultList.add(new DelOutboundBringVerifyVO("未知单号", false, e.getMessage()));
                }
            }
        }
        return resultList;
    }

    @Override
    public DelOutboundWrapperContext initContext(DelOutbound delOutbound) {
        String orderNo = delOutbound.getOrderNo();
        String warehouseCode = delOutbound.getWarehouseCode();
        // 查询地址信息
        DelOutboundAddress address = this.delOutboundAddressService.getByOrderNo(orderNo);
        if (null == address) {
            // 普通出口需要收货地址
            if (DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType())) {
                throw new CommonException("400", "收货地址信息不存在");
            }
        }
        // 查询sku信息
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);
        // 查询仓库信息
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
        if (null == warehouse) {
            throw new CommonException("400", "仓库信息不存在");
        }
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = null;
        if (null != address) {
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("400", "国家信息不存在");
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
                productList = this.baseProductClientService.queryProductList(conditionQueryDto);
                if (CollectionUtils.isEmpty(productList)) {
                    throw new CommonException("400", "查询SKU信息失败");
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
        // 包裹信息
        List<PackageInfo> packageInfos = new ArrayList<>();
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                    new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                    , Math.toIntExact(1), delOutbound.getOrderNo(), BigDecimal.ZERO, ""));
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
                    List<BaseProduct> basProductList = this.baseProductClientService.queryProductList(baseProductConditionQueryDto);
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
                    BaseProduct product = productMap.get(sku);
                    if (null == product) {
                        throw new CommonException("400", "查询SKU[" + sku + "]信息失败");
                    }
                    BigDecimal declaredValue;
                    if (null != product.getDeclaredValue()) {
                        declaredValue = BigDecimal.valueOf(product.getDeclaredValue());
                    } else {
                        declaredValue = BigDecimal.ZERO;
                    }
                    packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(product.getWeight()), "g"),
                            new Packing(Utils.valueOf(product.getLength()), Utils.valueOf(product.getWidth()), Utils.valueOf(product.getHeight()), "cm"),
                            Math.toIntExact(detail.getQty()), delOutbound.getOrderNo(), declaredValue, product.getProductAttribute()));
                    // 判断有没有包材
                    String bindCode = detail.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode)) {
                        BaseProduct baseProduct = bindCodeMap.get(bindCode);
                        if (null == baseProduct) {
                            throw new CommonException("400", "查询SKU[" + sku + "]的包材[" + bindCode + "]信息失败");
                        }
                        if (null != baseProduct.getDeclaredValue()) {
                            declaredValue = BigDecimal.valueOf(baseProduct.getDeclaredValue());
                        } else {
                            declaredValue = BigDecimal.ZERO;
                        }
                        packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(baseProduct.getWeight()), "g"),
                                new Packing(Utils.valueOf(baseProduct.getLength()), Utils.valueOf(baseProduct.getWidth()), Utils.valueOf(baseProduct.getHeight()), "cm"),
                                Math.toIntExact(detail.getQty()), delOutbound.getOrderNo(), declaredValue, ""));
                    }
                }
            } else if (PricingEnum.PACKAGE.equals(pricingEnum)) {
                // 批量出口传WMS返回的装箱明细过去计算
                if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())) {
                    // 查询装箱明细
                    List<DelOutboundPacking> packingList = this.delOutboundPackingService.packageListByOrderNo(delOutbound.getOrderNo(), DelOutboundPackingTypeConstant.TYPE_2);
                    if (CollectionUtils.isEmpty(packingList)) {
                        throw new CommonException("400", "没有查询到WMS返回的装箱信息");
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
                        BaseProduct product = productMap.get(sku);
                        if (null == product) {
                            throw new CommonException("400", "查询SKU[" + sku + "]信息失败");
                        }
                        BigDecimal productDeclaredValue;
                        if (null != product.getDeclaredValue()) {
                            productDeclaredValue = BigDecimal.valueOf(product.getDeclaredValue());
                        } else {
                            productDeclaredValue = BigDecimal.ZERO;
                        }
                        declareValue = declareValue.add(productDeclaredValue);
                    }
                    packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                            new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                            , Math.toIntExact(1), delOutbound.getOrderNo(), declareValue, ""));
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
        // 收货地址
        calcShipmentFeeCommand.setToAddress(new Address(address.getStreet1(),
                address.getStreet2(),
                address.getStreet3(),
                address.getPostCode(),
                address.getCity(),
                address.getStateOrProvince(),
                new CountryInfo(country.getAddressCode(), null, country.getEnName(), country.getName())
        ));
        // 发货地址
        calcShipmentFeeCommand.setFromAddress(new Address(warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getPostcode(),
                warehouse.getCity(),
                warehouse.getProvince(),
                new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName())
        ));
        // 联系信息
        calcShipmentFeeCommand.setToContactInfo(new ContactInfo(address.getConsignee(), address.getPhoneNo(), address.getEmail(), null));
        calcShipmentFeeCommand.setCalcTimeForDiscount(new Date());
        // 调用接口
        return this.htpPricedProductClientService.pricing(calcShipmentFeeCommand);
    }

    @Override
    public ShipmentOrderResult shipmentOrder(DelOutboundWrapperContext delOutboundWrapperContext) {
        DelOutbound delOutbound = delOutboundWrapperContext.getDelOutbound();
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
        // 改成uuid
        createShipmentOrderCommand.setReferenceNumber(UUID.randomUUID().toString().replaceAll("-", "").toUpperCase());
        createShipmentOrderCommand.setOrderNumber(delOutbound.getOrderNo());
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
                country.getEnName()));
        createShipmentOrderCommand.setReturnAddress(new AddressCommand(warehouse.getContact(),
                warehouse.getTelephone(),
                null,
                warehouse.getStreet1(),
                warehouse.getStreet2(),
                null,
                warehouse.getCity(),
                warehouse.getProvince(),
                warehouse.getPostcode(),
                warehouse.getCountryName()));
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
            packageItems.add(new PackageItem(delOutbound.getOrderNo(), delOutbound.getOrderNo(), Utils.valueOf(delOutbound.getAmount()), weightInGram = Utils.valueOfDouble(delOutbound.getWeight()),
                    new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                    1, "", String.valueOf(delOutbound.getId()), delOutbound.getOrderNo()));
        } else if (DelOutboundOrderTypeEnum.SPLIT_SKU.getCode().equals(delOutbound.getOrderType())) {
            List<String> skus = new ArrayList<>();
            skus.add(delOutbound.getNewSku());
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            conditionQueryDto.setSkus(skus);
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("400", "查询SKU[" + delOutbound.getNewSku() + "]信息失败");
            }
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
                    throw new CommonException("400", "查询SKU[" + sku + "]信息失败");
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
        if (weightInGram <= 0) {
            throw new CommonException("400", "包裹重量为0或者小于0，不能创建承运商物流订单");
        }
        packages.add(new Package(delOutbound.getOrderNo(), String.valueOf(delOutbound.getId()),
                new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                weightInGram, packageItems));
        createShipmentOrderCommand.setPackages(packages);
        createShipmentOrderCommand.setCarrier(new Carrier(delOutbound.getShipmentService()));
        ResponseObject<ShipmentOrderResult, ProblemDetails> responseObjectWrapper = this.htpCarrierClientService.shipmentOrder(createShipmentOrderCommand);
        if (null == responseObjectWrapper) {
            throw new CommonException("400", "创建承运商物流订单失败，调用承运商系统无响应");
        }
        if (responseObjectWrapper.isSuccess()) {
            // 保存挂号
            // 判断结果集是不是正确的
            ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
            if (null == shipmentOrderResult) {
                throw new CommonException("400", "创建承运商物流订单失败，调用承运商系统返回数据为空");
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
                    builder.append("创建承运商物流订单失败，调用承运商系统失败，返回错误信息为空");
                }
                throw new CommonException("400", builder.toString());
            }
            return shipmentOrderResult;
        } else {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError()), "创建承运商物流订单失败，调用承运商系统失败");
            throw new CommonException("400", exceptionMessage);
        }
    }

    @Override
    public boolean getShipmentLabel(DelOutbound delOutbound) {
        String orderNumber = delOutbound.getShipmentOrderNumber();
        // 获取标签
        CreateShipmentOrderCommand command = new CreateShipmentOrderCommand();
        command.setWarehouseCode(delOutbound.getWarehouseCode());
        command.setOrderNumber(orderNumber);
        DelOutboundOperationLogEnum.SMT_LABEL.listener(delOutbound);
        logger.info("正在获取标签文件，单号：{}", delOutbound.getOrderNo());
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
                        throw new CommonException("500", "创建文件夹[" + file.getPath() + "]失败，Error：" + e.getMessage());
                    }
                }
                byte[] inputStream;
                if (null != fileStream && null != (inputStream = fileStream.getInputStream())) {
                    File labelFile = new File(file.getPath() + "/" + orderNumber);
                    try {
                        FileUtils.writeByteArrayToFile(labelFile, inputStream, false);
                        return true;
                    } catch (IOException e) {
                        // 内部异常，不再重试，直接抛出去
                        throw new CommonException("500", "保存标签文件失败，Error：" + e.getMessage());
                    }
                }
            } else {
                // 接口响应异常，继续重试
                String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "获取标签文件流失败2");
                logger.error(exceptionMessage);
                throw new CommonException("500", exceptionMessage);
            }
        } else {
            // 接口响应异常继续重试
            logger.error("获取标签文件流失败");
            throw new CommonException("500", "获取标签文件流失败");
        }
        return false;
    }

    @Override
    public void htpShipmentLabel(DelOutbound delOutbound) {
        DelOutboundOperationLogEnum.SMT_SHIPMENT_LABEL.listener(delOutbound);
        String pathname = null;
        // 如果是批量出库，将批量出库上传的文件和标签文件合并在一起传过去
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType())
                && delOutbound.getIsLabelBox()) {
            // 判断文件是否已经创建
            String mergeFileDirPath = DelOutboundServiceImplUtil.getBatchMergeFilePath(delOutbound);
            File mergeFileDir = new File(mergeFileDirPath);
            if (!mergeFileDir.exists()) {
                try {
                    FileUtils.forceMkdir(mergeFileDir);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new CommonException("500", "创建文件夹失败，" + e.getMessage());
                }
            }
            String mergeFilePath = mergeFileDirPath + "/" + delOutbound.getOrderNo();
            File mergeFile = new File(mergeFilePath);
            if (!mergeFile.exists()) {
                // 合并文件
                // 查询上传文件
                // 查询上传文件信息
                BasAttachmentQueryDTO basAttachmentQueryDTO = new BasAttachmentQueryDTO();
                basAttachmentQueryDTO.setBusinessCode(AttachmentTypeEnum.DEL_OUTBOUND_BATCH_LABEL.getBusinessCode());
                basAttachmentQueryDTO.setBusinessNo(delOutbound.getOrderNo());
                R<List<BasAttachment>> listR = remoteAttachmentService.list(basAttachmentQueryDTO);
                if (null != listR && null != listR.getData()) {
                    List<BasAttachment> attachmentList = listR.getData();
                    if (CollectionUtils.isNotEmpty(attachmentList)) {
                        BasAttachment attachment = attachmentList.get(0);
                        // 箱标文件 - 上传的
                        String boxFilePath = attachment.getAttachmentPath() + "/" + attachment.getAttachmentName() + attachment.getAttachmentFormat();
                        String labelFilePath = "";
                        if ((DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel()))) {
                            // 批量出库的自提出库标签是上传的
                            // 查询上传的文件
                            basAttachmentQueryDTO = new BasAttachmentQueryDTO();
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
                        } else {
                            // 标签文件 - 从承运商物流那边获取的
                            labelFilePath = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber();
                        }
                        // 合并文件
                        try {
                            if (PdfUtil.merge(mergeFilePath, boxFilePath, labelFilePath)) {
                                pathname = mergeFilePath;
                            }
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                            throw new CommonException("500", "合并箱标文件，标签文件失败");
                        }
                    } else {
                        throw new CommonException("500", "箱标文件未上传");
                    }
                } else {
                    throw new CommonException("500", "箱标文件未上传");
                }
            }
        }
        if (null == pathname) {
            pathname = DelOutboundServiceImplUtil.getLabelFilePath(delOutbound) + "/" + delOutbound.getShipmentOrderNumber();
        }
        File labelFile = new File(pathname);
        if (!labelFile.exists()) {
            throw new CommonException("500", "标签文件不存在");
        }
        try {
            byte[] byteArray = FileUtils.readFileToByteArray(labelFile);
            String encode = cn.hutool.core.codec.Base64.encode(byteArray);
            ShipmentLabelChangeRequestDto shipmentLabelChangeRequestDto = new ShipmentLabelChangeRequestDto();
            shipmentLabelChangeRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
            shipmentLabelChangeRequestDto.setOrderNo(delOutbound.getOrderNo());
            shipmentLabelChangeRequestDto.setLabelType("ShipmentLabel");
            shipmentLabelChangeRequestDto.setLabel(encode);
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
    }

    @Override
    public void shipmentShipping(DelOutbound delOutbound) {
        ShipmentUpdateRequestDto shipmentUpdateRequestDto = new ShipmentUpdateRequestDto();
        shipmentUpdateRequestDto.setWarehouseCode(delOutbound.getWarehouseCode());
        shipmentUpdateRequestDto.setRefOrderNo(delOutbound.getOrderNo());
        if (DelOutboundOrderTypeEnum.BATCH.getCode().equals(delOutbound.getOrderType()) && "SelfPick".equals(delOutbound.getShipmentChannel())) {
            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
        } else {
            shipmentUpdateRequestDto.setShipmentRule(delOutbound.getShipmentRule());
        }
        shipmentUpdateRequestDto.setPackingRule(delOutbound.getPackingRule());
        shipmentUpdateRequestDto.setIsEx(false);
        shipmentUpdateRequestDto.setExType(null);
        shipmentUpdateRequestDto.setExRemark(null);
        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
        ResponseVO responseVO = htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
        if (null == responseVO || null == responseVO.getSuccess()) {
            throw new CommonException("400", "更新发货指令失败");
        }
        if (!responseVO.getSuccess()) {
            throw new CommonException("400", Utils.defaultValue(responseVO.getMessage(), "更新发货指令失败2"));
        }
    }

    @Override
    public void shipmentShippingEx(DelOutbound delOutbound, String exRemark) {
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
        shipmentUpdateRequestDto.setExType("OutboundGetTrackingFailed");
        shipmentUpdateRequestDto.setExRemark(Utils.defaultValue(exRemark, "操作失败"));
        shipmentUpdateRequestDto.setIsNeedShipmentLabel(false);
        htpOutboundClientService.shipmentShipping(shipmentUpdateRequestDto);
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
            throw new CommonException("400", "取消承运商物流订单失败");
        }
        CancelShipmentOrderBatchResult cancelShipmentOrderBatchResult = responseObject.getObject();
        List<CancelShipmentOrderResult> cancelOrders = cancelShipmentOrderBatchResult.getCancelOrders();
        for (CancelShipmentOrderResult cancelOrder : cancelOrders) {
            if (!cancelOrder.isSuccess()) {
                throw new CommonException("400", "取消承运商物流订单失败2");
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
        createShipmentRequestDto.setShipmentRule(delOutbound.getShipmentRule());
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
                throw new CommonException("400", "查询SKU信息失败");
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
                    String message = Utils.defaultValue(createShipmentResponseVO.getMessage(), "创建出库单失败2");
                    throw new CommonException("400", message);
                }
            }
            String message = Utils.defaultValue(createShipmentResponseVO.getErrors(), "创建出库单失败3");
            throw new CommonException("400", message);
        } else {
            throw new CommonException("400", "创建出库单失败");
        }
    }

}
