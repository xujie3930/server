package com.szmsd.doc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiSort;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 9:28
 */
@Api(tags = {"测试"})
@ApiSort(100)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping(value = "/token", produces = "application/json;charset=utf-8")
    public Object token() throws IOException {
        NameValuePair[] nameValuePairs = new NameValuePair[]{
                new BasicNameValuePair("client_id", "client"),
                new BasicNameValuePair("client_secret", "123456"),
                new BasicNameValuePair("grant_type", "password"),
                new BasicNameValuePair("username", "test"),
                new BasicNameValuePair("password", "123456")
        };
        return Request.Post("http://localhost:17001/oauth/token").bodyForm(nameValuePairs).execute().returnContent().asString(StandardCharsets.UTF_8);
    }

    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('read')")
    public String echo() {
        return "echo ... ";
    }
}
