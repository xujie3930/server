package com.szmsd.returnex;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.service.IReturnExpressService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @ClassName: com.szmsd.returnex.TestC
 * @Description:
 * @Author: 11
 * @Date: 2021/4/1 18:50
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestC {

    @Resource
    private IReturnExpressService returnExpressService;

    @Test
    public void te() {
        returnExpressService.expiredUnprocessedForecastOrder();
    }

    @Resource
    private ConfigStatus configStatus;
    @Test
    public void te2() {
        System.out.println(JSONObject.toJSONString(configStatus));
    }
}
