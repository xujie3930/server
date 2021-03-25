package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.CalcShipmentFeeCommand;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:30
 */
@Service
public class HtpPricedProductClientServiceImpl implements IHtpPricedProductClientService {

    @Autowired
    private HtpPricedProductFeignService htpPricedProductFeignService;

    @Override
    public ResponseObject<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command) {
        return R.getDataAndException(this.htpPricedProductFeignService.pricing(command));
    }
}
