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
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.*;
import com.szmsd.delivery.util.Utils;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.api.service.IHtpIBasClientService;
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
    private RechargesFeignService rechargesFeignService;
    @Autowired
    private IHtpOutboundClientService htpOutboundClientService;
    @Autowired
    private IHtpCarrierClientService htpCarrierClientService;
    @Autowired
    private IDelOutboundChargeService delOutboundChargeService;
    @Autowired
    private IHtpIBasClientService htpIBasClientService;

    @Override
    public int bringVerify(Long id) {
        // 根据id查询出库信息
        DelOutbound delOutbound = this.delOutboundService.getById(id);
        if (Objects.isNull(delOutbound)) {
            throw new CommonException("999", "单据不存在");
        }
        // 可以提审的单据类型：正常出库
        if (!(DelOutboundOrderTypeEnum.NORMAL.getCode().equals(delOutbound.getOrderType()))) {
            throw new CommonException("999", "单据类型不正确，不能提审");
        }
        // 可以提审的状态：待提审，审核失败
        if (!(DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState()))) {
            throw new CommonException("999", "单据状态不正确，不能提审");
        }
        ApplicationContext context = this.initContext(delOutbound);
        BringVerifyEnum currentState;
        String bringVerifyState = delOutbound.getBringVerifyState();
        if (StringUtils.isEmpty(bringVerifyState)) {
            currentState = BringVerifyEnum.BEGIN;
        } else {
            currentState = BringVerifyEnum.get(bringVerifyState);
        }
        new ApplicationContainer(context, currentState, BringVerifyEnum.END, BringVerifyEnum.BEGIN).action();
        return 1;
        // 修改单据状态为提审中
        /*Long id1 = delOutbound.getId();
        this.delOutboundService.updateState(id1, DelOutboundStateEnum.UNDER_REVIEW);
        try {
            // 调用接口
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = this.pricing(delOutboundWrapperContext);
            if (null == responseObject) {
                // 返回值是空的
                throw new CommonException("999", "计算包裹费用失败");
            } else {
                // 判断返回值
                if (responseObject.isSuccess()) {
                    // 计算成功了
                    ChargeWrapper chargeWrapper = responseObject.getObject();
                    DelOutbound updateDelOutbound = new DelOutbound();
                    updateDelOutbound.setId(id1);
                    // 更新：计费重，金额
                    ShipmentChargeInfo data = chargeWrapper.getData();
                    PricingPackageInfo packageInfo = data.getPackageInfo();
                    Weight calcWeight = packageInfo.getCalcWeight();
                    updateDelOutbound.setCalcWeight(calcWeight.getValue());
                    updateDelOutbound.setCalcWeightUnit(calcWeight.getUnit());
                    List<ChargeItem> charges = chargeWrapper.getCharges();
                    // 保存费用信息
                    List<DelOutboundCharge> delOutboundCharges = new ArrayList<>();
                    // 汇总费用
                    BigDecimal totalAmount = BigDecimal.ZERO;
                    String totalCurrencyCode = charges.get(0).getMoney().getCurrencyCode();
                    for (ChargeItem charge : charges) {
                        DelOutboundCharge delOutboundCharge = new DelOutboundCharge();
                        ChargeCategory chargeCategory = charge.getChargeCategory();
                        delOutboundCharge.setOrderNo(delOutbound.getOrderNo());
                        delOutboundCharge.setBillingNo(chargeCategory.getBillingNo());
                        delOutboundCharge.setChargeNameCn(chargeCategory.getChargeNameCN());
                        delOutboundCharge.setChargeNameEn(chargeCategory.getChargeNameEN());
                        delOutboundCharge.setParentBillingNo(chargeCategory.getParentBillingNo());
                        Money money = charge.getMoney();
                        BigDecimal amount = Utils.valueOf(money.getAmount());
                        delOutboundCharge.setAmount(amount);
                        delOutboundCharge.setCurrencyCode(money.getCurrencyCode());
                        delOutboundCharge.setRemark(charge.getRemark());
                        delOutboundCharges.add(delOutboundCharge);
                        totalAmount = totalAmount.add(amount);
                    }
                    this.delOutboundChargeService.saveCharges(delOutboundCharges);
                    // 冻结费用
                    CusFreezeBalanceDTO cusFreezeBalanceDTO = new CusFreezeBalanceDTO();
                    cusFreezeBalanceDTO.setAmount(totalAmount);
                    cusFreezeBalanceDTO.setCurrencyCode(totalCurrencyCode);
                    cusFreezeBalanceDTO.setCusCode(delOutbound.getSellerCode());
                    R<?> freezeBalanceR = this.rechargesFeignService.freezeBalance(cusFreezeBalanceDTO);
                    if (null != freezeBalanceR) {
                        if (Constants.SUCCESS == freezeBalanceR.getCode()) {
                            // 获取产品信息
                            String productCode = data.getProductCode();
                            PricedProductInfo pricedProductInfo = this.htpPricedProductClientService.info(productCode);
                            if (null != pricedProductInfo) {
                                // 保存获取挂号方式
                                updateDelOutbound.setTrackingAcquireType(pricedProductInfo.getTrackingAcquireType());
                                String trackingNo = null;
                                // 调用新增/修改发货规则
                                AddShipmentRuleRequest addShipmentRuleRequest = new AddShipmentRuleRequest();
                                addShipmentRuleRequest.setShipmentRule(productCode);
                                addShipmentRuleRequest.setGetLabelType(pricedProductInfo.getTrackingAcquireType());
                                BaseOperationResponse baseOperationResponse = this.htpIBasClientService.shipmentRule(addShipmentRuleRequest);
                                if (null == baseOperationResponse || null == baseOperationResponse.getSuccess()) {
                                    throw new CommonException("999", "新增/修改发货规则失败");
                                }
                                if (!baseOperationResponse.getSuccess()) {
                                    String message = baseOperationResponse.getMessage();
                                    if (StringUtils.isEmpty(message)) {
                                        message = "新增/修改发货规则失败";
                                    }
                                    throw new CommonException("999", message);
                                }
                                // 判断是否需要创建物流订单
                                if (DelOutboundTrackingAcquireTypeEnum.ORDER_SUPPLIER.getCode().equals(pricedProductInfo.getTrackingAcquireType())) {
                                    // 创建承运商物流订单
                                    ShipmentOrderResult shipmentOrderResult = this.shipmentOrder(delOutboundWrapperContext);
                                    updateDelOutbound.setTrackingNo(trackingNo = shipmentOrderResult.getMainTrackingNumber());
                                    updateDelOutbound.setShipmentOrderNumber(shipmentOrderResult.getOrderNumber());
                                }
                                // 推单到WMS
                                updateDelOutbound.setRefOrderNo(this.shipmentCreate(delOutboundWrapperContext, trackingNo));
                            } else {
                                // 异常信息
                                throw new CommonException("999", "查询产品[" + productCode + "]信息失败");
                            }
                        } else {
                            // 异常信息
                            String msg = Utils.defaultValue(freezeBalanceR.getMsg(), "冻结费用信息失败");
                            throw new CommonException("999", msg);
                        }
                    } else {
                        // 异常信息
                        throw new CommonException("999", "冻结费用信息失败");
                    }
                    // 计算提审成功
                    updateDelOutbound.setAmount(totalAmount);
                    updateDelOutbound.setCurrencyCode(totalCurrencyCode);
                    this.delOutboundService.bringVerifySuccess(updateDelOutbound);
                    return 1;
                } else {
                    // 计算失败
                    String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObject.getError()), "计算包裹费用失败2");
                    throw new CommonException("999", exceptionMessage);
                }
            }
        } catch (CommonException e) {
            logger.error(e.getMessage(), e);
            String exceptionMessage = e.getMessage();
            exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
            this.delOutboundService.bringVerifyFail(id1, exceptionMessage);
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 提审失败
            String exceptionMessage = "提审操作失败";
            this.delOutboundService.bringVerifyFail(id1, exceptionMessage);
            throw new CommonException("999", exceptionMessage);
        }*/
    }

    @Override
    public DelOutboundWrapperContext initContext(DelOutbound delOutbound) {
        String orderNo = delOutbound.getOrderNo();
        String warehouseCode = delOutbound.getWarehouseCode();
        // 查询地址信息
        DelOutboundAddress address = this.delOutboundAddressService.getByOrderNo(orderNo);
        if (null == address) {
            throw new CommonException("999", "收货地址信息不存在");
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
        R<BasRegionSelectListVO> countryR = this.basRegionFeignService.queryByCountryCode(address.getCountryCode());
        BasRegionSelectListVO country = R.getDataAndException(countryR);
        if (null == country) {
            throw new CommonException("999", "国家信息不存在");
        }
        // 查询sku信息
        BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
        List<String> skus = new ArrayList<>();
        for (DelOutboundDetail detail : detailList) {
            skus.add(detail.getSku());
        }
        // conditionQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
        conditionQueryDto.setSkus(skus);
        List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
        if (CollectionUtils.isEmpty(productList)) {
            throw new CommonException("999", "查询SKU信息失败");
        }
        return new DelOutboundWrapperContext(delOutbound, address, detailList, warehouse, country, productList);
    }

    @Override
    public ResponseObject<ChargeWrapper, ProblemDetails> pricing(DelOutboundWrapperContext delOutboundWrapperContext) {
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
        Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
        // 包裹信息
        List<PackageInfo> packageInfos = new ArrayList<>();
        for (DelOutboundDetail detail : detailList) {
            String sku = detail.getSku();
            BaseProduct product = productMap.get(sku);
            if (null == product) {
                throw new CommonException("999", "查询SKU[" + sku + "]信息失败");
            }
            packageInfos.add(new PackageInfo(new Weight(Utils.valueOf(product.getWeight()), "g"),
                    new Packing(Utils.valueOf(product.getLength()), Utils.valueOf(product.getWidth()), Utils.valueOf(product.getHeight()), "cm"),
                    Math.toIntExact(detail.getQty()), delOutbound.getOrderNo(), BigDecimal.ZERO));
        }
        // 计算包裹费用
        CalcShipmentFeeCommand calcShipmentFeeCommand = new CalcShipmentFeeCommand();
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
        // 查询sku信息
        List<BaseProduct> productList = delOutboundWrapperContext.getProductList();
        Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
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
            return responseObjectWrapper.getObject();
        } else {
            String exceptionMessage = Utils.defaultValue(ProblemDetails.getErrorMessageOrNull(responseObjectWrapper.getError()), "创建承运商物流订单失败2");
            throw new CommonException("999", exceptionMessage);
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
        createShipmentRequestDto.setRemark(delOutbound.getRemark());
        createShipmentRequestDto.setRefOrderNo(delOutbound.getOrderNo());
        createShipmentRequestDto.setAddress(new ShipmentAddressDto(address.getConsignee(),
                address.getCountryCode(), country.getName(), address.getZone(), address.getStateOrProvince(), address.getCity(),
                address.getStreet1(), address.getStreet2(), address.getStreet3(), address.getPostCode(), address.getPhoneNo(), address.getEmail()));
        List<ShipmentDetailInfoDto> details = new ArrayList<>();
        for (DelOutboundDetail detail : detailList) {
            details.add(new ShipmentDetailInfoDto(detail.getSku(), detail.getQty(), detail.getNewLabelCode()));
        }
        createShipmentRequestDto.setDetails(details);
        createShipmentRequestDto.setIsPackingByRequired(delOutbound.getIsPackingByRequired());
        createShipmentRequestDto.setIsFirst(delOutbound.getIsFirst());
        createShipmentRequestDto.setNewSKU(delOutbound.getNewSku());
        CreateShipmentResponseVO createShipmentResponseVO = this.htpOutboundClientService.shipmentCreate(createShipmentRequestDto);
        if (null != createShipmentResponseVO && null != createShipmentResponseVO.getSuccess()) {
            if (createShipmentResponseVO.getSuccess()) {
                return createShipmentResponseVO.getOrderNo();
            } else {
                String message = createShipmentResponseVO.getMessage();
                if (StringUtils.isEmpty(message)) {
                    message = "创建出库单失败";
                }
                throw new CommonException("999", message);
            }
        } else {
            throw new CommonException("999", "创建出库单失败");
        }
    }

}
