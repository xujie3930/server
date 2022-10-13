package com.szmsd.finance;

import com.alibaba.fastjson.JSON;
import com.helipay.demo.common.component.util.api.encrypt.HelipayAPIEncrypt;
import com.helipay.demo.common.component.util.secure.AES;
import com.szmsd.finance.vo.helibao.PayCallback;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class PayCallbackTest {

    public static void main(String[] args) {

        String paysign = "KOqz1xq9YPyz7uy6";

        String payaes = "qf84+sxJfoEUwTC/5Sz7Ww==";


        PayCallback payCallback = new PayCallback();

        payCallback.setBindId("123123");
        payCallback.setMerchantNo("Me10047065");
        payCallback.setOrderNo("20221013742693");
        payCallback.setProductCode("WXPAYSCAN");
        payCallback.setSerialNumber("W0722101300006076");
        payCallback.setRemark("备注");
        payCallback.setCreateDate(new Date());
        payCallback.setGoodsName("账户充值-CNY373-124212");
        payCallback.setFinishDate(new Date());
        payCallback.setConsumeOrderId("215324532532");
        payCallback.setChanlType("WANGLIAN");
        payCallback.setOrderStatus("SUCCESS");
        payCallback.setOrderAmount(new BigDecimal(2000));

        Map map = (Map) JSON.toJSON(payCallback);

        String sign = HelipayAPIEncrypt.sign(map, paysign);
        String aesValue = AES.encryptToBase64(map.toString(), payaes);

        System.out.println(sign);
        System.out.println("============");
        System.out.println(aesValue);


    }
}