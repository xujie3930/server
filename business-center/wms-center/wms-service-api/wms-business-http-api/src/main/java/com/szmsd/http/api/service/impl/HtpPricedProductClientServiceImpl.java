package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;
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
    public ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command) {
        return R.getDataAndException(this.htpPricedProductFeignService.pricing(command));
    }

    @Override
    public List<PricedProduct> inService(PricedProductInServiceCriteria criteria) {
        return R.getDataAndException(this.htpPricedProductFeignService.inService(criteria));
    }
}
