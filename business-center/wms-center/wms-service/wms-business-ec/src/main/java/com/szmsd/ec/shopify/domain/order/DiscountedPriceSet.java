package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName DiscountedPriceSet.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class DiscountedPriceSet {
    private ShopMoney shop_money;
    private PresentmentMoney presentment_money;
}