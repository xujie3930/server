package com.szmsd.ec.shopify.domain.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName OriginLocation.java
 * @Description ----------功能描述---------
 * @Date 2021-04-13 13:48
 * @Author hyx
 * @Version 1.0
 */
@NoArgsConstructor
@Data
public class OriginLocation {
    private long id;
    private String country_code;
    private String province_code;
    private String name;
    private String address1;
    private String address2;
    private String city;
    private String zip;
}