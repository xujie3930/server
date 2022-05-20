package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @FileName OrderResponse.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:47
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class OrderResponse {
    private List<ShopifyOrders> orders;
}