package com.szmsd.ec.shopify.domain.ship;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @FileName Fulfillment.java
 * @Description ----------功能描述---------
 * @Date 2021-04-21 9:39
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class ShopifyFulfillment {
    private long location_id;
    private List<String> tracking_numbers;
    private List<ShopifyLineItem> line_items;
    private List<String> tracking_urls;
    private String tracking_company;
}