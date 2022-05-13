package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName BillingAddress.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class BillingAddress {
    private String first_name;
    private String address1;
    private String phone;
    private String city;
    private String zip;
    private String province;
    private String country;
    private String last_name;
    private String address2;
    private String company;
    private double latitude;
    private double longitude;
    private String name;
    private String country_code;
    private String province_code;
}