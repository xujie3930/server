package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName TotalTaxSet.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class TotalTaxSet {
    private ShopMoney shop_money;
    private PresentmentMoney presentment_money;
}