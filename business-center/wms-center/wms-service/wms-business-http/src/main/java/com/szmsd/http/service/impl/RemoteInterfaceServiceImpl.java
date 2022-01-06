package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.DomainConfig;
import com.szmsd.http.config.DomainInterceptorConfig;
import com.szmsd.http.config.DomainPluginConfig;
import com.szmsd.http.config.DomainURIUtil;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import com.szmsd.http.plugins.Domain;
import com.szmsd.http.plugins.DomainInterceptor;
import com.szmsd.http.plugins.DomainPlugin;
import com.szmsd.http.service.ICommonRemoteService;
import com.szmsd.http.service.RemoteInterfaceService;
import com.szmsd.http.util.DomainUtil;
import com.szmsd.http.vo.HttpResponseVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RemoteInterfaceServiceImpl implements RemoteInterfaceService {
    private final Logger logger = LoggerFactory.getLogger(RemoteInterfaceServiceImpl.class);

    @Autowired
    private DomainConfig domainConfig;
    @Autowired
    private DomainInterceptorConfig domainInterceptorConfig;
    @Autowired
    private DomainPluginConfig domainPluginConfig;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public HttpResponseVO rmi(HttpRequestDto dto) {
        HttpResponseVO responseVO = new HttpResponseVO();
        Date requestTime = new Date();
        String uri = dto.getUri();
        String domain;
        // 处理uri
        // uri格式${xxx}/api/user
        if (uri.startsWith(DomainUtil.PREFIX)) {
            int i = uri.indexOf(DomainUtil.SUFFIX);
            if (i < 0) {
                throw new CommonException("500", "环境变量配置错误，没有'}'");
            }
            String domainKey = uri.substring(2, i);
            if (StringUtils.isEmpty(domainKey)) {
                throw new CommonException("500", "域名key配置错误，不能为空");
            }
            domain = this.domainConfig.getDomain(domainKey);
            String api = uri.substring(i + 1);
            uri = domain + api;
        } else {
            domain = DomainURIUtil.getDomain(uri);
        }
        Map<String, String> requestHeaders = dto.getHeaders();
        if (MapUtils.isEmpty(requestHeaders)) requestHeaders = new HashMap<>();
        // 请求body
        String requestBody = JSON.toJSONString(dto.getBody());
        List<DomainInterceptor> domainInterceptorList = null;
        try {
            // 处理拦截器逻辑
            List<String> interceptors = this.domainInterceptorConfig.getInterceptors(domain);
            if (CollectionUtils.isNotEmpty(interceptors)) {
                domainInterceptorList = new ArrayList<>();
                boolean isBreak = false;
                for (String interceptor : interceptors) {
                    DomainInterceptor domainInterceptor = getDomainInterceptor(interceptor);
                    // 处理之前的拦截器，返回false不执行
                    if (!domainInterceptor.preHandle(uri, requestHeaders, requestBody)) {
                        isBreak = true;
                    }
                    domainInterceptorList.add(domainInterceptor);
                }
                if (isBreak) {
                    HttpResponseVO httpResponseVO = new HttpResponseVO();
                    httpResponseVO.setStatus(200);
                    httpResponseVO.setBody("拦截返回");
                    return httpResponseVO;
                }
            }
            // 处理插件逻辑
            List<String> handlerPlugins = new ArrayList<>();
            handlerPlugins.add("DefaultDomainPlugin");
            List<String> plugins = this.domainPluginConfig.getPlugins(domain);
            if (CollectionUtils.isNotEmpty(plugins)) {
                handlerPlugins.addAll(plugins);
            }
            for (String plugin : handlerPlugins) {
                DomainPlugin domainPlugin = getDomainPlugin(plugin, domain);
                requestHeaders = domainPlugin.headers(requestHeaders);
                uri = domainPlugin.uri(uri);
                requestBody = domainPlugin.requestBody(requestBody);
            }
            // 二进制
            Boolean binary = dto.getBinary();
            if (null == binary) {
                // default value
                binary = false;
            }
            // 处理请求体
            HttpEntityEnclosingRequestBase request = null;
            if (HttpMethod.GET.equals(dto.getMethod())) {
                String params = HttpClientHelper.builderGetParams(requestBody);
                if (StringUtils.isNotEmpty(params)) {
                    if (uri.lastIndexOf("?") == -1) {
                        uri = uri + "?";
                    }
                    uri = uri + params;
                }
                request = new HttpClientHelper.HttpGet(uri);
            } else if (HttpMethod.POST.equals(dto.getMethod())) {
                request = new HttpPost(uri);
            } else if (HttpMethod.PUT.equals(dto.getMethod())) {
                request = new HttpPut(uri);
            } else if (HttpMethod.DELETE.equals(dto.getMethod())) {
                request = new HttpClientHelper.HttpDelete(uri);
            }
            if (null == request) {
                throw new CommonException("500", "不支持的类型：" + dto.getMethod());
            }
            // 执行请求
            HttpResponseBody httpResponseBody = HttpClientHelper.executeOnByteArray(request, requestBody, requestHeaders);
            if (httpResponseBody instanceof HttpResponseBody.HttpResponseByteArrayWrapper) {
                HttpResponseBody.HttpResponseByteArrayWrapper byteArrayWrapper = (HttpResponseBody.HttpResponseByteArrayWrapper) httpResponseBody;
                responseVO.setStatus(byteArrayWrapper.getStatus());
                responseVO.setHeaders(headerToMap(byteArrayWrapper.getHeaders()));
                if (binary) {
                    responseVO.setBody(byteArrayWrapper.getByteArray());
                    responseVO.setBinary(true);
                } else {
                    String body = new String(byteArrayWrapper.getByteArray(), StandardCharsets.UTF_8);
                    responseVO.setBody(body);
                    responseVO.setBinary(false);
                }
            } else if (httpResponseBody instanceof HttpResponseBody.HttpResponseBodyEmpty) {
                HttpResponseBody.HttpResponseBodyEmpty responseBodyEmpty = (HttpResponseBody.HttpResponseBodyEmpty) httpResponseBody;
                responseVO.setStatus(0);
                String body = responseBodyEmpty.getBody();
                if (null == body) {
                    body = "";
                }
                String responseBody = "请求失败，" + body;
                responseVO.setBody(responseBody.getBytes(StandardCharsets.UTF_8));
                responseVO.setBinary(false);
            }
            if (CollectionUtils.isNotEmpty(domainInterceptorList)) {
                for (DomainInterceptor domainInterceptor : domainInterceptorList) {
                    domainInterceptor.postHandle(uri, responseVO.getBody());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            responseVO.setStatus(0);
            responseVO.setBody(("请求失败，" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        } finally {
            if (CollectionUtils.isNotEmpty(domainInterceptorList)) {
                for (DomainInterceptor domainInterceptor : domainInterceptorList) {
                    domainInterceptor.afterCompletion(uri, responseVO.getBody());
                }
            }
            // 记录日志
            Date responseTime = new Date();
            HtpRequestLog requestLog = new HtpRequestLog();
            requestLog.setRemark("" + responseVO.getStatus());
            requestLog.setTraceId(MDC.get("TID"));
            requestLog.setRequestUri(uri);
            requestLog.setRequestMethod(dto.getMethod().name());
            requestLog.setRequestHeader(JSON.toJSONString(requestHeaders));
            requestLog.setRequestBody(requestBody);
            requestLog.setRequestTime(requestTime);
            requestLog.setResponseHeader(JSON.toJSONString(responseVO.getHeaders()));
            Object body = responseVO.getBody();
            if (Objects.nonNull(body)) {
                if (body instanceof String) {
                    requestLog.setResponseBody((String) body);
                } else {
                    String responseBody = new String((byte[]) body, StandardCharsets.UTF_8);
                    requestLog.setResponseBody(responseBody);
                }
            }
            requestLog.setResponseTime(responseTime);
            EventUtil.publishEvent(new RequestLogEvent(requestLog));
        }
        return responseVO;
    }

    private DomainPlugin getDomainPlugin(String plugin, String domain) {
        DomainPlugin domainPlugin = this.applicationContext.getBean(plugin, DomainPlugin.class);
        if (domainPlugin instanceof Domain) {
            ((Domain) domainPlugin).setDomain(domain);
        }
        return domainPlugin;
    }

    private DomainInterceptor getDomainInterceptor(String interceptor) {
        return this.applicationContext.getBean(interceptor, DomainInterceptor.class);
    }

    private Map<String, String> headerToMap(Header[] headers) {
        Map<String, String> map = new HashMap<>();
        if (ArrayUtils.isNotEmpty(headers)) {
            for (Header header : headers) {
                map.put(header.getName(), header.getValue());
            }
        }
        return map;
    }

    @Resource
    private ICommonRemoteService iCommonRemoteService;

    @Override
    public void rmiSync(HttpRequestSyncDTO dto) {
        iCommonRemoteService.insertRmiOne(dto);
    }
}
