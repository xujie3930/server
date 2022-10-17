package com.szmsd.finance.service.impl;

import com.szmsd.finance.service.RemoteService;
import com.szmsd.finance.vo.helibao.Base64;
import com.szmsd.finance.vo.helibao.FormatType;
import com.szmsd.finance.util.MarshallerUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Service("remoteService")
public class RemoteServiceImpl implements RemoteService {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceImpl.class);
    private static final String HTTPS_PREFIX = "https";

    @Override
    public <T> T postRemoteInvoke(String remoteUrl, Object requestForm, Class clazz) {
        return remoteInvoke(remoteUrl, requestForm, clazz, HttpMethod.POST, false, null);
    }


    @Override
    public <T> T remoteInvoke(String remoteUrl, Object requestForm, Class clazz, HttpMethod method, boolean needAuth, Map<String, ?> authParams) {
        RestTemplate restTemplate;
        if (StringUtils.startsWith(remoteUrl, HTTPS_PREFIX)) {
            restTemplate = getHttpsRestTemplate();
        } else {
            restTemplate = getHttpRestTemplate();
        }
        String response;

        if (HttpMethod.GET.equals(method)) {
            response = restTemplate.getForObject(remoteUrl, String.class, new HttpEntity<>(httpHeaders(needAuth, authParams)));
        } else {
            //默认post请求
            response = restTemplate.postForObject(remoteUrl, new HttpEntity<>(requestForm, httpHeaders(needAuth, authParams)), String.class);
        }

        logger.info("远程调用 {} 结果 {}", remoteUrl, response);
        if (StringUtils.isBlank(response)) {
            return null;
        }

        T t = null;
        try {
            t = (T) MarshallerUtils.unmarshal(FormatType.json, response, clazz);
        } catch (Throwable e) {
            logger.error("远程调用失败，调用地址：" + remoteUrl, e);
        }
        return t;
    }


    public RestTemplate getHttpRestTemplate() {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setReadTimeout(60000);
        simpleClientHttpRequestFactory.setConnectTimeout(5000);
        return new RestTemplate(simpleClientHttpRequestFactory);
    }

    public RestTemplate getHttpsRestTemplate() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(60000);
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


    private HttpHeaders httpHeaders(boolean needAuth, Map<String, ?> authParams) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        if (needAuth) {
            headers.add("Authorization", "Basic " + base64Creds(authParams));
        }
        return headers;
    }

    private String base64Creds(Map<String, ?> authParams) {
        final String plainCreds = authParams.get("username") + ":" + authParams.get("password");
        final byte[] plainCredsBytes = plainCreds.getBytes();
        final byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);

    }


    @Override
    public String httpRequestPostMethod(String content, String url) throws IOException {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);
        method.setRequestHeader("Content-type", "application/json;charset=UTF-8");
        if (content != null) {
            RequestEntity requestEntity = new StringRequestEntity(content, MediaType.APPLICATION_JSON_UTF8_VALUE, "UTF-8");
            method.setRequestEntity(requestEntity);
        }
        client.getParams().setHttpElementCharset("UTF-8");
        client.getParams().setContentCharset("UTF-8");

        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        client.getHttpConnectionManager().getParams().setSoTimeout(30000);
        client.executeMethod(method);
        int statusCode = method.getStatusLine().getStatusCode();
        logger.info("httpRequst httpStatus = " + statusCode + "]");
        String result = "";
        try {
            result = new String(method.getResponseBody(), "UTF-8");
        } finally {
            try {
                if (method != null) {
                    method.abort();
                    method.releaseConnection();
                }
            } catch (Exception e) {
                logger.error("", e);
            }
            method = null;
        }
        return result.toString();
    }

}
