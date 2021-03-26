package com.szmsd.http.api.service;

import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;

import java.util.List;

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

    /**
     * 根据客户代码国家等信息获取可下单产品
     *
     * @param criteria criteria
     * @return PricedProduct
     */
    List<PricedProduct> inService(PricedProductInServiceCriteria criteria);
}
