package com.szmsd.ec.shopify.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName ShopifyConfig.java
 * @Description ----------功能描述---------
 * @Date 2021-04-14 14:15
 * @Author hyx
 * @Version 1.0
 */
@Slf4j
public class ShopifyConfig {

    public static final String HTTPS = "https://";

    public static final String orderUrl = "/admin/api/2022-04/orders.json";
    /**
     * 物流回传地址
     */
    public static final String fulfillmentUrl = "/admin/api/2022-04/orders/{ORDER_ID}/fulfillments.json" ;

    /**
     * 创建履约单地址
     */
    public static final String createFulfillmentUrl = "/admin/api/2022-04/fulfillments.json";

    /**
     * 获取location URL
     */
    public static final String getLocationUrl = "/admin/api/2022-04/locations.json";

    /***付款已经过授权，订单已准备好进行发货，但订单中商品尚未发运***/
    public static final String UNSHIPPED ="Unshipped";
    public static final String SHIPPED ="Shipped";
    public static final String CANCEL ="Canceled";
    public static final String EXCEPTION ="Exception";
    public static final String DELIVERING ="Delivering";
    public static final String COMPELETED ="Compeleted";
    /****时间转换格式*/
    public static final String TIME_FORMAT_STR = "yyyy-MM-dd'T'HH:mm:ss";

    public static String requestUrl(String uri, String shop){
        return HTTPS + shop + uri;
    }

    /**
     * 获取locationId
     * @param shopName
     * @param accessToken
     * @return
     */
    public static String getLocationId(String shopName,String accessToken){
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("X-Shopify-Access-Token", accessToken);
        HttpResponseBody responseBody = HttpClientHelper.httpGet(requestUrl(getLocationUrl,shopName), "", headerMap);
        if(responseBody == null ){
            log.info("【Shopify】店铺{}获取location信息失败！", shopName);
            return "";
        }
        String body = responseBody.getBody();
        log.info("【Shopify】店铺{}获取location信息: {}", shopName, body);
        if (StringUtils.isNotBlank(body)) {
            JSONObject jsonObject = JSONObject.parseObject(body);
            JSONArray locationsArr = jsonObject.getJSONArray("locations");
            if (locationsArr == null) {
                JSONObject obj = locationsArr.getObject(0, JSONObject.class);
                return obj.getString("id");
            }
        }
        log.info("【Shopify】店铺{} 未获取到location信息！", shopName);
        return "";
    }
}