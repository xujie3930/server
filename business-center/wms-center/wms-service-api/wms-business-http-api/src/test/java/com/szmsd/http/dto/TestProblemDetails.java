package com.szmsd.http.dto;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

/**
 * @author zhangyuyuan
 * @date 2021-03-27 14:34
 */
public class TestProblemDetails {

    @Test
    public void toProblemDetails() {
        String text = "{\"errors\":{\"calcTimeForDiscount\":[\"Unexpected character encountered while parsing value: 1. Path 'calcTimeForDiscount', line 1, position 24.\"]},\"type\":\"https://tools.ietf.org/html/rfc7231#section-6.5.1\",\"title\":\"One or more validation errors occurred.\",\"status\":400,\"traceId\":\"|96f8f836-4fe45bcd78abbf1f.\"}";
        ProblemDetails problemDetails = JSON.parseObject(text, ProblemDetails.class);
        System.out.println(JSON.toJSONString(problemDetails));
    }

    @Test
    public void toProblemDetails2() {
        String text = "{\"TicketId\":\"d89f9276-532a-46d8-b6b8-0daae070181f\",\"UtcDateTime\":\"2021-03-27T06:21:26.4564993Z\",\"RequestUri\":\"http://pricedproduct-internalapi-external.dsloco.com/api/products/Pricing\",\"Errors\":[{\"Sn\":null,\"Code\":\"800F2069\",\"Message\":\"Shipment of goods (GeneralCargo) is not supported by this service\"}]}";
        ProblemDetails problemDetails = JSON.parseObject(text, ProblemDetails.class);
        System.out.println(JSON.toJSONString(problemDetails));
    }
}
