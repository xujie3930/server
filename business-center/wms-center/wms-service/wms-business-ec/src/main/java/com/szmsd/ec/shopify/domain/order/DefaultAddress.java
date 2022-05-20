package com.szmsd.ec.shopify.domain.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName DefaultAddress.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class DefaultAddress {
    private long id;
    private long customer_id;
    private String first_name;
    private String last_name;
    private String company;
    private String address1;
    private String address2;
    private String city;
    private String province;
    private String country;
    private String zip;
    private String phone;
    private String name;
    private String province_code;
    private String country_code;
    private String country_name;
    @JSONField(name = "default")
    private boolean defaultX;
}