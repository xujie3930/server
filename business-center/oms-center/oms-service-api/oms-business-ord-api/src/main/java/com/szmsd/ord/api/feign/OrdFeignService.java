package com.szmsd.ord.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.ord.api.domain.OrdOrder;
import com.szmsd.ord.api.domain.Order;
import com.szmsd.ord.api.domain.OrderDto;
import com.szmsd.ord.api.domain.OrderVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-30 14:03
 * @Description
 */
@FeignClient(name = "business-ord")
@RequestMapping(value = "order", produces = {"application/json;charset=UTF-8"})
public interface OrdFeignService {


    /**
     * 查询订单
     *
     * @param orderDto
     * @return
     */
    @PostMapping(value = "ord-order/getOrderList")
    R<List<OrderVo>> getOrderList(@RequestBody OrderDto orderDto);
    
}
