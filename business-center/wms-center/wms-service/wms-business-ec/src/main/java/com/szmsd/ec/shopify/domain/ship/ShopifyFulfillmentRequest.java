package com.szmsd.ec.shopify.domain.ship;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName ShopifyFulfillmentRequest.java
 * @Description ----------功能描述---------
 * @Date 2021-04-21 9:39
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ShopifyFulfillmentRequest {
    private ShopifyFulfillment fulfillment;
}