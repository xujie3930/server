package com.szmsd.bas;

import com.szmsd.bas.config.HMacSHA256;
import com.szmsd.bas.util.ShopifyUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class TestShopifyUtil {

    @Test
    public void test() {
        Map<String, String[]> parameterMap = new HashMap<>();
        // {"shop_id":"57241337917","shop_domain":"test5-dm-fulfillment.myshopify.com"}

        // {"shop_id":"64164856023","shop_domain":"test2-dm-fulfillment.myshopify.com"}
        parameterMap.put("shop_id", new String[]{"20344406038"});
        parameterMap.put("shop_domain", new String[]{"cambridgetestshop.myshopify.com"});
        final String encryptParameter = ShopifyUtil.encryptParameterBase64(parameterMap, "2d68bfaaa40cbbda541bf948219e5a8e");
        System.out.println(encryptParameter);
    }

    @Test
    public void test2() {
        String s = "{\"shop_id\":\"57241337917\",\"shop_domain\":\"test5-dm-fulfillment.myshopify.com\"}";
        String encryptBase64 = HMacSHA256.encryptBase64("2d68bfaaa40cbbda541bf948219e5a8e", s);
        System.out.println(encryptBase64);
    }

}
