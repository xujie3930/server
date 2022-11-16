package com.szmsd.bas;

import cn.hutool.http.HttpUtil;
import org.junit.Test;

public class HttpTest {

    @Test
    public void postTest(){

        String url = "https://web-client-1.dsloco.com/api/wms-business-finance/helibao/pay-callback";

        String body = "{\"content\":\"FmC/Om+vg7XZsL11UktSzjtMYj1RQfhn54n0Wwg+d01Z4QcE4j01ttIv42NhQlj6JN1ks5J8xMigF9v/LBcy/TB36y3z9bl8pA7OBcmccrfXxMraDDOfkymsPZz0f4+gaK5MvhPPxwbl4G76u9hiZQTapclJHEyqhT6jVF2OFFJE8GdnQuUobdFCswSVh4AnCcADy0lCLTqUEkmDkVwPTpTAIthFF0aBf0D1Dg6YTm8xfTH5nIWnpXdn9KcEJABUhnZP2GwpJr+lGNCUINJcfP1+JnhOOUHCfyD5VinmVpKKSlHvgBHb9o2JthUHo3av01O7uqS9zVTsYxs/tQv/iTD0/XknG8MSrcnPGlh78PM=\",\"merchantNo\":\"Me10047065\",\"orderNo\":\"2022111602192479\",\"productCode\":\"WXPAYSCAN\",\"sign\":\"0e378164302ac51e04b2030982a5c38bc4deb37ded10d7a8bf89d7e238670478\"}";

        String result = HttpUtil.post(url,body);

        System.out.println(result);

    }
}
