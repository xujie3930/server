package com.szmsd.delivery.service.wrapper.impl;

import com.szmsd.bas.api.domain.BasCountry;
import com.szmsd.bas.api.feign.BasCountryFeignService;
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
import com.szmsd.delivery.service.IDelOutboundDetailService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
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
    private BasCountryFeignService basCountryFeignService;
    @Autowired
    private BaseProductClientService baseProductClientService;

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
        // 查询地址信息
        String orderNo = delOutbound.getOrderNo();
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
        BasWarehouse warehouse = this.basWarehouseClientService.queryByWarehouseCode(delOutbound.getWarehouseCode());
        if (null == warehouse) {
            throw new CommonException("999", "仓库信息不存在");
        }
        // 查询国家信息，收货地址所在的国家
        R<BasCountry> countryR = this.basCountryFeignService.queryByCountryCode(address.getCountryCode());
        BasCountry country = R.getDataAndException(countryR);
        if (null == country) {
            throw new CommonException("999", "国家信息不存在");
        }
        // 修改单据状态为提审中
        Long id1 = delOutbound.getId();
        this.delOutboundService.updateState(id1, DelOutboundStateEnum.UNDER_REVIEW);
        try {
            // 计算包裹费用
            CalcShipmentFeeCommand command = new CalcShipmentFeeCommand();
            // 产品代码就是选择的物流承运商
            command.setProductCode(delOutbound.getShipmentRule());
            command.setClientCode(delOutbound.getCustomCode());
            command.setShipmentType(delOutbound.getShipmentType());
            // 包裹信息
            List<PackageInfo> packageInfos = new ArrayList<>();
            BaseProductConditionQueryDto conditionQueryDto = new BaseProductConditionQueryDto();
            List<String> skus = new ArrayList<>();
            for (DelOutboundDetail detail : detailList) {
                skus.add(detail.getSku());
            }
            conditionQueryDto.setWarehouseCode(delOutbound.getWarehouseCode());
            conditionQueryDto.setSkus(skus);
            // 查询sku信息
            List<BaseProduct> productList = this.baseProductClientService.queryProductList(conditionQueryDto);
            if (CollectionUtils.isEmpty(productList)) {
                throw new CommonException("999", "查询SKU信息失败");
            }
            Map<String, BaseProduct> productMap = productList.stream().collect(Collectors.toMap(BaseProduct::getCode, (v) -> v, (v1, v2) -> v1));
            for (DelOutboundDetail detail : detailList) {
                String sku = detail.getSku();
                BaseProduct product = productMap.get(sku);
                if (null == product) {
                    throw new CommonException("999", "SKU[" + sku + "]信息不存在");
                }
                packageInfos.add(new PackageInfo(new Weight(valueOf(product.getWeight()), "g"),
                        new Packing(valueOf(product.getLength()), valueOf(product.getWidth()), valueOf(product.getHeight()), "cm"),
                        Math.toIntExact(detail.getQty()), orderNo, BigDecimal.ZERO));
            }
            command.setPackageInfos(packageInfos);
            // 收货地址
            command.setToAddress(new Address(address.getStreet1(),
                    address.getStreet2(),
                    address.getStreet3(),
                    address.getPostCode(),
                    address.getCity(),
                    address.getStateOrProvince(),
                    new CountryInfo(country.getCountryCode(), null, country.getCountryNameEn(), country.getCountryName())
            ));
            // 发货地址
            command.setFromAddress(new Address(warehouse.getStreet1(),
                    warehouse.getStreet2(),
                    null,
                    warehouse.getPostcode(),
                    warehouse.getCity(),
                    warehouse.getProvince(),
                    new CountryInfo(warehouse.getCountryCode(), null, warehouse.getCountryName(), warehouse.getCountryChineseName())
            ));
            // 联系信息
            command.setToContactInfo(new ContactInfo(address.getConsignee(), address.getPhoneNo(), address.getEmail(), null));
            command.setCalcTimeForDiscount(new Date());
            // 调用接口
            ResponseObject<ChargeWrapper, ProblemDetails> responseObject = this.htpPricedProductClientService.pricing(command);
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
                    ChargeItem chargeItem = charges.get(0);
                    Money money = chargeItem.getMoney();
                    updateDelOutbound.setAmount(valueOf(money.getAmount()));
                    updateDelOutbound.setCurrencyCode(money.getCurrencyCode());
                    this.delOutboundService.bringVerifySuccess(updateDelOutbound);
                    return 1;
                } else {
                    // 计算失败
                    String exceptionMessage = null;
                    ProblemDetails problemDetails = responseObject.getError();
                    if (null != problemDetails) {
                        List<ErrorDto2> errors = problemDetails.getErrors();
                        if (CollectionUtils.isNotEmpty(errors)) {
                            ErrorDto2 errorDto2 = errors.get(0);
                            if (StringUtils.isNotEmpty(errorDto2.getCode())) {
                                exceptionMessage = "[" + errorDto2.getCode() + "]" + errorDto2.getMessage();
                            } else {
                                exceptionMessage = errorDto2.getMessage();
                            }
                        }
                    }
                    if (StringUtils.isEmpty(exceptionMessage)) {
                        exceptionMessage = "计算包裹费用失败";
                    }
                    exceptionMessage = StringUtils.substring(exceptionMessage, 0, 255);
                    this.delOutboundService.bringVerifyFail(id1, exceptionMessage);
                    throw new CommonException("999", exceptionMessage);
                }
            }
        } catch (CommonException e) {
            throw e;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 回滚状态
            this.delOutboundService.updateState(id1, DelOutboundStateEnum.AUDIT_FAILED);
            throw new CommonException("999", "提审操作失败");
        }
    }

    private BigDecimal valueOf(Double value) {
        if (null == value) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }
}
