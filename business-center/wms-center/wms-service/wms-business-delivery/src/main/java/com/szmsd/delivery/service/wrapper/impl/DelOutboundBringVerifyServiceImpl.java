package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.Utils;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.Package;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Autowired
    private BasRegionFeignService basRegionFeignService;
    @Autowired
    private BaseProductClientService baseProductClientService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;

    @Override
    public int bringVerify(DelOutboundBringVerifyDto dto) {
        List<Long> ids = dto.getIds();
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        // 根据id查询出库信息
        List<DelOutbound> delOutboundList = this.delOutboundService.listByIds(ids);
        if (CollectionUtils.isNotEmpty(delOutboundList)) {
            for (DelOutbound delOutbound : delOutboundList) {
                if (Objects.isNull(delOutbound)) {
                    throw new CommonException("999", "单据不存在");
                }
                // 可以提审的状态：待提审，审核失败
                boolean isAuditFailed = DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState());
                if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || isAuditFailed)) {
                    throw new CommonException("999", "单据状态不正确，不能提审");
                }
                ApplicationContext context = this.initContext(delOutbound);
                BringVerifyEnum currentState;
                String bringVerifyState = delOutbound.getBringVerifyState();
                if (StringUtils.isEmpty(bringVerifyState)) {
                    currentState = BringVerifyEnum.BEGIN;
                } else {
                    currentState = BringVerifyEnum.get(bringVerifyState);
                    // 兼容
                    if (null == currentState) {
                        currentState = BringVerifyEnum.BEGIN;
                    }
                }
                ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, BringVerifyEnum.END, BringVerifyEnum.BEGIN);
                try {
                    applicationContainer.action();
                } catch (CommonException e) {
                    // 回滚操作
                    applicationContainer.setEndState(BringVerifyEnum.BEGIN);
                    applicationContainer.rollback();
                    // 抛出异常
                    throw e;
                }
            }
        }
        return delOutboundList.size();
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
                throw new CommonException("999", "收货地址信息不存在");
            }
        }
        // 查询sku信息
        List<DelOutboundDetail> detailList = this.delOutboundDetailService.listByOrderNo(orderNo);
        if (CollectionUtils.isEmpty(detailList)) {
            throw new CommonException("999", "出库明细不存在");
        }
        // 查询仓库信息
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(warehouseCode);
        if (null == warehouse) {
            throw new CommonException("999", "仓库信息不存在");
        }
        // 查询国家信息，收货地址所在的国家
        BasRegionSelectListVO country = null;
        if (null != address) {
            R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
            country = R.getDataAndException(countryR);
            if (null == country) {
                throw new CommonException("999", "国家信息不存在");
            }
        }
        // 查询sku信息
        BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
        List<String> skus = new ArrayList<>();
        for (DelOutboundDetail detail : detailList) {
            skus.add(detail.getSku());
        }
        // conditionQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
        conditionQueryDto.setSkus(skus);
        List<BaseProduct> productList = null;
        // 转运出库的不查询sku明细信息
        if (!DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("999", "查询SKU信息失败");
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
                        throw new CommonException("999", "查询SKU[" + sku + "]信息失败");
                    }
                    packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(product.getWeight()), "g"),
                            new Packing(Utils.valueOf(product.getLength()), Utils.valueOf(product.getWidth()), Utils.valueOf(product.getHeight()), "cm"),
                            Math.toIntExact(detail.getQty()), delOutbound.getOrderNo(), BigDecimal.ZERO, product.getProductAttribute()));
                    // 判断有没有包材
                    String bindCode = detail.getBindCode();
                    if (StringUtils.isNotEmpty(bindCode)) {
                        BaseProduct baseProduct = bindCodeMap.get(bindCode);
                        if (null == baseProduct) {
                            throw new CommonException("999", "查询SKU[" + sku + "]的包材[" + bindCode + "]信息失败");
                        }
                        packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(baseProduct.getWeight()), "g"),
                                new Packing(Utils.valueOf(baseProduct.getLength()), Utils.valueOf(baseProduct.getWidth()), Utils.valueOf(baseProduct.getHeight()), "cm"),
                                Math.toIntExact(detail.getQty()), delOutbound.getOrderNo(), BigDecimal.ZERO, ""));
                    }
                }
            } else if (PricingEnum.PACKAGE.equals(pricingEnum)) {
                packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(delOutbound.getWeight()), "g"),
                        new Packing(Utils.valueOf(delOutbound.getLength()), Utils.valueOf(delOutbound.getWidth()), Utils.valueOf(delOutbound.getHeight()), "cm")
                        , Math.toIntExact(1), delOutbound.getOrderNo(), BigDecimal.ZERO, ""));
            }
        }
        // 计算包裹费用
        CalcShipmentFeeCommand calcShipmentFeeCommand = new CalcShipmentFeeCommand();
        // true代表需要验证，false的话，主要是用于测算
        calcShipmentFeeCommand.setAddressValifition(true);
        // 产品代码就是选择的物流承运商
        calcShipmentFeeCommand.setProductCode(delOutbound.getShipmentRule());
        calcShipmentFeeCommand.setClientCode(delOutbound.getCustomCode());
        calcShipmentFeeCommand.setShipmentType(delOutbound.getShipmentType());
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
        createShipmentOrderCommand.setReferenceNumber(String.valueOf(delOutbound.getId()));
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
        // 包裹信息
        List<Package> packages = new ArrayList<>();
        List<PackageItem> packageItems = new ArrayList<>();
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            packageItems.add(new PackageItem(delOutbound.getOrderNo(), Utils.valueOf(delOutbound.getAmount()), Utils.valueOfDouble(delOutbound.getWeight()),
                    new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                    1, "", String.valueOf(delOutbound.getId()), delOutbound.getOrderNo()));
        } else {
            // 查询sku信息
            List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("999", "查询SKU[" + sku + "]信息失败");
                }
                packageItems.add(new PackageItem(sku, product.getDeclaredValue(), Utils.valueOfDouble(product.getWeight()),
                        new Size(product.getLength(), product.getWidth(), product.getHeight()),
                        Utils.valueOfLong(detail.getQty()), "", String.valueOf(detail.getId()), sku));
            }
        }
        packages.add(new Package(delOutbound.getOrderNo(), String.valueOf(delOutbound.getId()),
                new Size(delOutbound.getLength(), delOutbound.getWidth(), delOutbound.getHeight()),
                Utils.valueOfDouble(delOutbound.getWeight()), packageItems));
        createShipmentOrderCommand.setPackages(packages);
        createShipmentOrderCommand.setCarrier(new Carrier(delOutbound.getShipmentService()));
        ResponseObject<ShipmentOrderResult, ProblemDetails> responseObjectWrapper = this.htpCarrierClientService.shipmentOrder(createShipmentOrderCommand);
        if (null == responseObjectWrapper) {
            throw new CommonException("999", "创建承运商物流订单失败");
        }
        if (responseObjectWrapper.isSuccess()) {
            // 保存挂号
            // 判断结果集是不是正确的
            ShipmentOrderResult shipmentOrderResult = responseObjectWrapper.getObject();
            if (null == shipmentOrderResult) {
                throw new CommonException("999", "创建承运商物流订单失败3");
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
                    builder.append("创建承运商物流订单失败4");
                }
                throw new CommonException("999", builder.toString());
            }
            return shipmentOrderResult;
        } else {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError()), "创建承运商物流订单失败2");
            throw new CommonException("999", exceptionMessage);
        }
    }

    @Override
    public void cancellation(String referenceNumber, String shipmentOrderNumber, String trackingNo) {
        CancelShipmentOrderCommand command = new CancelShipmentOrderCommand();
        command.setReferenceNumber(referenceNumber);
        List<CancelShipmentOrder> cancelShipmentOrders = new ArrayList<>();
        cancelShipmentOrders.add(new CancelShipmentOrder(shipmentOrderNumber, trackingNo));
        command.setCancelShipmentOrders(cancelShipmentOrders);
        ResponseObject<CancelShipmentOrderBatchResult, ErrorDataDto> responseObject = this.htpCarrierClientService.cancellation(command);
        if (null == responseObject || !responseObject.isSuccess()) {
            throw new CommonException("999", "取消承运商物流订单失败");
        }
        CancelShipmentOrderBatchResult cancelShipmentOrderBatchResult = responseObject.getObject();
        List<CancelShipmentOrderResult> cancelOrders = cancelShipmentOrderBatchResult.getCancelOrders();
        for (CancelShipmentOrderResult cancelOrder : cancelOrders) {
            if (!cancelOrder.isSuccess()) {
                throw new CommonException("999", "取消承运商物流订单失败2");
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
        if (DelOutboundOrderTypeEnum.SELF_PICK.getCode().equals(delOutbound.getOrderType())) {
            createShipmentRequestDto.setTrackingNo(delOutbound.getDeliveryInfo());
            createShipmentRequestDto.setShipmentRule(delOutbound.getDeliveryAgent());
        }
        createShipmentRequestDto.setRemark(delOutbound.getRemark());
        createShipmentRequestDto.setRefOrderNo(delOutbound.getOrderNo());
        if (null != address) {
            createShipmentRequestDto.setAddress(new ShipmentAddressDto(address.getConsignee(),
                    address.getCountryCode(), country.getName(), address.getZone(), address.getStateOrProvince(), address.getCity(),
                    address.getStreet1(), address.getStreet2(), address.getStreet3(), address.getPostCode(), address.getPhoneNo(), address.getEmail()));
        }
        // 转运出库明细处理
        List<ShipmentDetailInfoDto> details;
        if (DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode().equals(delOutbound.getOrderType())) {
            details = new ArrayList<>();
            // 转运出库，明细不传
            /*for (DelOutboundDetail detail : detailList) {
                details.add(new ShipmentDetailInfoDto(detail.getProductName(), detail.getQty(), detail.getNewLabelCode()));
            }*/
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
            }
            details = new ArrayList<>(shipmentDetailInfoDtoMap.values());
        }
        createShipmentRequestDto.setDetails(details);
        createShipmentRequestDto.setIsPackingByRequired(delOutbound.getIsPackingByRequired());
        createShipmentRequestDto.setIsFirst(delOutbound.getIsFirst());
        createShipmentRequestDto.setNewSKU(delOutbound.getNewSku());
        CreateShipmentResponseVO createShipmentResponseVO = this.htpOutboundClientService.shipmentCreate(createShipmentRequestDto);
        if (null != createShipmentResponseVO) {
            if (null != createShipmentResponseVO.getSuccess()) {
                if (createShipmentResponseVO.getSuccess()) {
                    return createShipmentResponseVO.getOrderNo();
                } else {
                    String message = Utils.defaultValue(createShipmentResponseVO.getMessage(), "创建出库单失败2");
                    throw new CommonException("999", message);
                }
            }
            String message = Utils.defaultValue(createShipmentResponseVO.getErrors(), "创建出库单失败3");
            throw new CommonException("999", message);
        } else {
            throw new CommonException("999", "创建出库单失败");
        }
    }

}
