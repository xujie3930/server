package com.szmsd.doc.controller;

import com.szmsd.common.core.domain.R;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 9:28
 */
@Api(tags = {"授权测试"})
@ApiSort(100)
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${server.port}")
    private int port;

    @Autowired
    private RestTemplate restTemplate;


    @ApiOperation(value = "获取Token信息", position = 100, notes = "1.本接口只适用于测试环境<br/>" +
            "2.先请求 [GET]http://127.0.0.1:9200/oauth/authorize?client_id=doc&response_type=code&scope=server&redirect_uri=http://www.baidu.com 登录成功之后，复制code, redirect_uri 信息<br/>" +
            "3.将 code, redirect_uri 传到这个接口来请求<br/>" +
            "4.将返回的 access_token 作为token请求资源接口。<br/>" +
            "<br/>" +
            "第三方系统联调<br/>" +
            "请求顺序<br/>" +
            "1.第三方本地判断有没有 access_token 信息，没有走第2步，有走第4步（或直接请求资源接口）。<br/>" +
            "2.第三方本地判断有没有 refresh_token 信息，没有走第3步，有走第5步（或直接走第3步）。<br/>" +
            "3.第三方系统打开浏览器访问 [GET]http://接口系统地址/oauth/authorize?client_id=doc&response_type=code&scope=server&redirect_uri=回调地址 ，登录成功之后记录 code 信息。<br/>" +
            "第三方系统获取到 code 信息之后请求 [POST]http://接口系统地址/oauth/token 获取 token 信息。<br/>" +
            "请求参数信息：client_id: doc<br/>" +
            "client_secret: 123456<br/>" +
            "grant_type: authorization_code<br/>" +
            "code: code信息<br/>" +
            "redirect_uri: 回调地址<br/>" +
            "4.根据 access_token 验证是否有效，[POST]http://接口系统地址/oauth/check_token 请求参数：token: access_token的值，请求成功表示可以正常访问接口系统。访问失败走第2步。<br/>" +
            "5.根据 refresh_token 刷新token信息，[POST]http://接口系统地址/oauth/token 请求参数：client_id: doc<br/>" +
            "client_secret: 123456<br/>" +
            "grant_type: refresh_token<br/>" +
            "refresh_token: refresh_token的值<br/>" +
            "请求成功之后会返回新的 access_token 信息，请求失败走第3步。")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "编码", dataType = "String", required = true),
            @ApiImplicitParam(name = "redirect_uri", value = "回调地址", dataType = "String", required = true)
    })
    @GetMapping(value = "/token", produces = "application/json;charset=utf-8")
    public R<Object> token(@RequestParam("code") String code, @RequestParam("redirect_uri") String redirect_uri) throws IOException {
        MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
        requestParam.set("client_id", "doc");
        requestParam.set("client_secret", "123456");
        requestParam.set("grant_type", "authorization_code");
        requestParam.set("code", code);
        requestParam.set("redirect_uri", redirect_uri);
        OAuth2AccessToken oAuth2AccessToken = restTemplate.postForObject("http://szmsd-auth/oauth/token", requestParam, OAuth2AccessToken.class);
        return R.ok(oAuth2AccessToken);
    }

    @GetMapping("/echo")
    @PreAuthorize("hasAuthority('client')")
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
