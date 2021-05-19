package com.szmsd.http.service;

import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-05-19 14:39
 */
public class TestRequest {

    @Test
    public void test1() {

        String url = "https://wms-open-api2.dsloco.com/api/base/shipmentrule";
        String requestBody = "{\"getLabelType\":\"WarehouseSupplier\",\"shipmentRule\":\"UARLE\"}";
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("UserId", "oms1");
        headerMap.put("Password", "123");
        HttpResponseBody responseBody = HttpClientHelper.httpPost(url, requestBody, headerMap);
        System.out.println(responseBody.getStatus());
        System.out.println(responseBody.getBody());
    }
}
