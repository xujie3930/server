package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @FileName LineItems.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class LineItems {
    private long id;
    private String admin_graphql_api_id;
    private int fulfillable_quantity;
    private String fulfillment_service;
    private String fulfillment_status;
    private String gift_card;
    private int grams;
    private String name;
    private OriginLocation origin_location;
    private String price;
    private PriceSet price_set;
    private boolean product_exists;
    private long product_id;
    private int quantity;
    private boolean requires_shipping;
    private String sku;
    private boolean taxable;
    private String title;
    private String total_discount;
    private TotalDiscountSet total_discount_set;
    private long variant_id;
    private String variant_inventory_management;
    private String variant_title;
    private String vendor;
    private List<String> properties;
    private List<String> tax_lines;
    private List<String> duties;
    private List<String> discount_allocations;
}