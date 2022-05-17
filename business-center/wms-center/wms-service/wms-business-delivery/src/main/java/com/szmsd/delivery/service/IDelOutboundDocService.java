package com.szmsd.delivery.service;

import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.http.vo.PricedProduct;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:29
 */
public interface IDelOutboundDocService {

    List<DelOutboundAddResponse> add(List<DelOutboundDto> list);

    /**
     * 产品服务列表
     *
     * @param dto dto
     * @return PricedProduct
     */
    List<PricedProduct> inService(DelOutboundOtherInServiceDto dto);

    /**
     * 产品服务列表2, 不根据传参国家查询  根据仓库国家查询
     *
     * @param dto dto
     * @return PricedProduct
     */
    List<PricedProduct> inService2(DelOutboundOtherInServiceDto dto);

    /**
     * 服务验证
     *
     * @param dto          dto
     * @param shipmentRule shipmentRule
     * @return boolean
     */
    boolean inServiceValid(DelOutboundOtherInServiceDto dto, String shipmentRule);
}
