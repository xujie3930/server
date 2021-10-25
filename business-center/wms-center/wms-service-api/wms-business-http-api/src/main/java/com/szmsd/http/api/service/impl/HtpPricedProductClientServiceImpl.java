package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.PricedProductInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:30
 */
@Service
public class HtpPricedProductClientServiceImpl implements IHtpPricedProductClientService {

    @Autowired
    private HtpPricedProductFeignService htpPricedProductFeignService;

    @Override
    public PricedProductInfo info(String productCode) {
        return R.getDataAndException(this.htpPricedProductFeignService.info(productCode));
    }

    @Override
    public PricedProductInfo infoAndSubProducts(String productCode) {
        PricedProductInfo pricedProductInfo = this.info(productCode);
        List<String> subProducts = pricedProductInfo.getSubProducts();
        if (StringUtils.isEmpty(pricedProductInfo.getLogisticsRouteId()) && CollectionUtils.isNotEmpty(subProducts)) {
            return this.info(subProducts.get(0));
        }
        return pricedProductInfo;
    }

    @Override
    public ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command) {
        return R.getDataAndException(this.htpPricedProductFeignService.pricing(command));
    }

    @Override
    public List<PricedProduct> inService(PricedProductInServiceCriteria criteria) {
        return R.getDataAndException(this.htpPricedProductFeignService.inService(criteria));
    }
}
