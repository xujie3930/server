package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @FileName ShippingLines.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ShippingLines {
    private long id;
    private String carrier_identifier;
    private String code;
    private String delivery_category;
    private String discounted_price;
    private DiscountedPriceSet discounted_price_set;
    private String phone;
    private String price;
    private PriceSet price_set;
    private String requested_fulfillment_service_id;
    private String source;
    private String title;
    private List<Object> tax_lines;
    private List<Object> discount_allocations;
}