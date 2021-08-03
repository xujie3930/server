package com.szmsd.doc.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiSort;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 9:28
 */
@Api(tags = {"测试"})
@ApiSort(100)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${server.port}")
    private int port;

    @GetMapping(value = "/token", produces = "application/json;charset=utf-8")
    public Object token() throws IOException {
        NameValuePair[] nameValuePairs = new NameValuePair[]{
                new BasicNameValuePair("client_id", "client"),
                new BasicNameValuePair("client_secret", "123456"),
                new BasicNameValuePair("grant_type", "password"),
                new BasicNameValuePair("username", "test"),
                new BasicNameValuePair("password", "123456")
        };
        String hostAddress = getLocalHostLANAddress().getHostAddress();
        return Request.Post("http://" + hostAddress + ":" + port + "/oauth/token").bodyForm(nameValuePairs).execute().returnContent().asString(StandardCharsets.UTF_8);
    }

    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('read')")
    public String echo() {
        return "echo ... ";
    }

    private InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<?> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<?> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
