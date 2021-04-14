package com.szmsd.http.config;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.service.http.WmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 20:51
 */
// @Component
public class HttpApplicationRunner implements ApplicationRunner {

    @Autowired
    private HttpConfig httpConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        System.out.println(JSON.toJSONString(httpConfig));

        TestWmsRequest testWmsRequest = new TestWmsRequest(httpConfig);
        System.out.println(testWmsRequest.listing());

    }

    static class TestWmsRequest extends WmsRequest {

        public TestWmsRequest(HttpConfig httpConfig) {
            super(httpConfig);
        }

        public Object listing() {
            Map<String, Object> map = new HashMap<>();
            map.put("PageSize", 10);
            map.put("PageIndex", 1);
            return httpGet(null, "inventory.listing", map);
        }
    }
}
