package com.szmsd.http.api.service;

import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.PricedProductInfo;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:29
 */
public interface IHtpPricedProductClientService {

    /**
     * 根据产品代码获取产品信息
     *
     * @param productCode productCode
     * @return PricedProductInfo
     */
    PricedProductInfo info(String productCode);

    /**
     * 根据产品代码获取产品信息，如果存在子产品信息，则返回第一个子产品的信息
     *
     * @param productCode productCode
     * @return PricedProductInfo
     */
    PricedProductInfo infoAndSubProducts(String productCode);

    /**
     * 计算包裹的费用
     *
     * @param command command
     * @return ResponseObject<ChargeWrapper, ProblemDetails>
     */
    ResponseObject.ResponseObjectWrapper<ChargeWrapper, ProblemDetails> pricing(CalcShipmentFeeCommand command);

    /**
     * 根据客户代码国家等信息获取可下单产品
     *
     * @param criteria criteria
     * @return PricedProduct
     */
    List<PricedProduct> inService(PricedProductInServiceCriteria criteria);

    /**
     * 分页查询产品列表，返回指定页面的数据，以及统计总记录数
     *
     * @param pricedProductSearchCriteria pricedProductSearchCriteria
     * @return PricedProduct
     */
    PageVO<PricedProduct> pageResult(PricedProductSearchCriteria pricedProductSearchCriteria);
}
