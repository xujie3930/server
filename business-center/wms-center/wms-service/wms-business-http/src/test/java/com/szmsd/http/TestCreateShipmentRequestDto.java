package com.szmsd.http;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.service.TestService;
import com.szmsd.http.service.TestServiceImpl;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zhangyuyuan
 * @date 2021-03-11 9:20
 */
public class TestCreateShipmentRequestDto {

    @Test
    public void jsonToJavaObject() {
        String json = "{\"warehouseCode\":\"W001\",\"orderType\":\"Noraml\"}";

        String requestClass = "com.szmsd.http.dto.CreateShipmentRequestDto";
        try {
            Class<?> forName = Class.forName(requestClass);

            Object object = JSON.parseObject(json, forName);
            System.out.println(JSON.toJSONString(object));

            TestService testService = new TestServiceImpl();
            String method = "create";
            Object invokeMethod = MethodUtils.invokeMethod(testService, method, object);
            System.out.println(JSON.toJSONString(invokeMethod));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
