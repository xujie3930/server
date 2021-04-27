package com.szmsd.http.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 17:24
 */
@Getter
@AllArgsConstructor
public enum HttpUrlType {

    WMS("wms"),
    PRICED_PRODUCT("pricedProduct"),
    CARRIER_SERVICE("carrierService"),
    PRODUCT_REMOTE_AREA("productRemoteArea"),
    THIRD_PAYMENT("thirdPayment"),
    ;
    private final String key;
}
