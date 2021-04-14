package com.szmsd.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhangyuyuan
 * @date 2021-04-14 9:32
 */
public class TestStrUtil {

    @Test
    public void TesttoUpperCase() {
        // api
        // base-info.seller         ->>> baseInfo.seller
        // base-info.shipment-rule  ->>> baseInfo.shipmentRule
        // exception.processing     ->>> exception.processing
        Assert.assertEquals(toUpperCase("base-info.seller"), "baseInfo.seller");
        Assert.assertEquals(toUpperCase("base-info.shipment-rule"), "baseInfo.shipmentRule");
        Assert.assertEquals(toUpperCase("exception.processing"), "exception.processing");
    }

    public String toUpperCase(String api) {
        if (api.contains("-")) {
            String[] strs = api.split("-");
            StringBuilder builder = new StringBuilder(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                String str = strs[i];
                builder.append(str.substring(0, 1).toUpperCase())
                        .append(str.substring(1));
            }
            api = builder.toString();
        }
        return api;
    }
}
