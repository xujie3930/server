package com.szmsd.bas;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.szmsd.bas.config.HMacSHA256;
import com.szmsd.bas.util.ShopifyUtil;
import org.junit.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestShopifyUtil {

    @Test
    public void test() {
        // 068d53f1f7c5e7f57c36436e96545678
        // 2d68bfaaa40cbbda541bf948219e5a8e

        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        // {"shop_id":"57241337917","shop_domain":"test5-dm-fulfillment.myshopify.com"}

        // {"shop_id":"64164856023","shop_domain":"test2-dm-fulfillment.myshopify.com"}
        parameterMap.put("shop_id", new String[]{"57241337917"});
        parameterMap.put("shop_domain", new String[]{"test5-dm-fulfillment.myshopify.com"});
        final String encryptParameter = ShopifyUtil.encryptParameterBase64(parameterMap, "2d68bfaaa40cbbda541bf948219e5a8e");
        System.out.println(encryptParameter);
    }

    @Test
    public void test2() throws NoSuchAlgorithmException, InvalidKeyException {
        String message = "{\"shop_id\":\"57241337917\",\"shop_domain\":\"test5-dm-fulfillment.myshopify.com\"}";
        String secret = "2d68bfaaa40cbbda541bf948219e5a8e";
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] bytes = hmacSHA256.doFinal(message.getBytes(StandardCharsets.UTF_8));
        System.out.println(org.apache.commons.codec.binary.Base64.encodeBase64String(bytes));
        String base64 = Base64.getEncoder().encodeToString(bytes);
        System.out.println(base64);
    }

    @Test
    public void test3() {
        String clientSecret = "2d68bfaaa40cbbda541bf948219e5a8e";

        Map<String, Object> parameterMap = new LinkedHashMap<>();
        parameterMap.put("shop_id", 57241337917L);
        parameterMap.put("shop_domain", "test5-dm-fulfillment.myshopify.com");
        String pretty = JSON.toJSONString(parameterMap, SerializerFeature.PrettyFormat,
                SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty);
        System.out.println(pretty);

        String message = JSON.toJSONString(parameterMap);
        System.out.println(message);

        System.out.println(HMacSHA256.encryptBase64(clientSecret, pretty));
        System.out.println(HMacSHA256.encryptBase64(clientSecret, message));
    }

}
