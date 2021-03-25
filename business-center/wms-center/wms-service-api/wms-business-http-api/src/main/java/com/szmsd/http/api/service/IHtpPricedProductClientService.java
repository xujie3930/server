package com.szmsd.http.api.service;

import com.szmsd.http.dto.CalcShipmentFeeCommand;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:29
 */
public interface IHtpPricedProductClientService {

    /**
     * 计算包裹的费用
     *
     * @param command command
     * @return ResponseObject<ChargeWrapper, ProblemDetails>
     */
    ResponseObject<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command);
}
