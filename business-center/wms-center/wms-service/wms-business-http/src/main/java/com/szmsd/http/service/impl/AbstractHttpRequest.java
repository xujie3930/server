package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.annotation.LogIgnore;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 14:00
 */
public abstract class AbstractHttpRequest {

    protected HttpConfig httpConfig;

    public AbstractHttpRequest(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    abstract String getUrl();

    abstract Map<String, String> getHeaderMap();

    HttpResponseBody httpRequestBody(String api, Object object, HttpMethod httpMethod, String... pathVariable) {
        String url = getUrl() + api;
        url = pathVariable(url, pathVariable);
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        HttpResponseBody responseBody;
        if (HttpMethod.POST.equals(httpMethod)) {
            responseBody = HttpClientHelper.httpPost(url, requestBody, headerMap);
        } else if (HttpMethod.PUT.equals(httpMethod)) {
            responseBody = HttpClientHelper.httpPut(url, requestBody, headerMap);
        } else if (HttpMethod.DELETE.equals(httpMethod)) {
            responseBody = HttpClientHelper.httpDelete(url, requestBody, headerMap);
        } else {
            throw new CommonException("999", "未处理的请求方式");
        }
        String logRequestBody;
        if (object.getClass().isAnnotationPresent(LogIgnore.class)) {
            LogIgnore logIgnore = object.getClass().getAnnotation(LogIgnore.class);
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            filter.getExcludes().addAll(Arrays.asList(logIgnore.value()));
            logRequestBody = JSON.toJSONString(object, filter);
        } else {
            logRequestBody = requestBody;
        }
        addLog(url, httpMethod.name(), headerMap, logRequestBody, requestTime, responseBody.getBody());
        return responseBody;
    }

    String httpRequest(String api, Object object, HttpMethod httpMethod, String... pathVariable) {
        return this.httpRequestBody(api, object, httpMethod, pathVariable).getBody();
    }

    String httpPost(String api, Object object) {
        return this.httpRequest(api, object, HttpMethod.POST);
    }

    String httpPut(String api, Object object) {
        return this.httpRequest(api, object, HttpMethod.PUT);
    }

    String httpDelete(String api, Object object) {
        return this.httpRequest(api, object, HttpMethod.DELETE);
    }

    String httpPost(String api, Object object, String... pathVariable) {
        return this.httpRequest(api, object, HttpMethod.POST, pathVariable);
    }

    String httpPut(String api, Object object, String... pathVariable) {
        return this.httpRequest(api, object, HttpMethod.PUT, pathVariable);
    }

    String httpDelete(String api, Object object, String... pathVariable) {
        return this.httpRequest(api, object, HttpMethod.DELETE, pathVariable);
    }

    HttpResponseBody httpPostBody(String api, Object object) {
        return this.httpRequestBody(api, object, HttpMethod.POST);
    }

    HttpResponseBody httpPutBody(String api, Object object) {
        return this.httpRequestBody(api, object, HttpMethod.PUT);
    }

    HttpResponseBody httpDeleteBody(String api, Object object) {
        return this.httpRequestBody(api, object, HttpMethod.DELETE);
    }

    FileStream httpPostFile(String api, Object object) {
        String url = getUrl() + api;
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String requestBody = JSON.toJSONString(object);
        // 调用获取文件流的方法
        FileStream responseBody = null;
        HttpResponseBody httpResponseBody = HttpClientHelper.httpPostStream(url, headerMap, requestBody);
        if (httpResponseBody instanceof HttpResponseBody.HttpResponseByteArrayWrapper) {
            responseBody = new FileStream();
            HttpResponseBody.HttpResponseByteArrayWrapper httpResponseByteArrayWrapper = (HttpResponseBody.HttpResponseByteArrayWrapper) httpResponseBody;
            Header[] headers = httpResponseByteArrayWrapper.getHeaders();
            byte[] byteArray = httpResponseByteArrayWrapper.getByteArray();
            if (ArrayUtils.isNotEmpty(headers)) {
                for (Header header : headers) {
                    if (null == header) {
                        continue;
                    }
                    if ("Content-Disposition".equals(header.getName())) {
                        responseBody.setContentDisposition(header.getValue());
                    }
                }
            }
            if (null != byteArray) {
                responseBody.setInputStream(byteArray);
            }
        }
        addLog(url, "POST", headerMap, requestBody, requestTime, "FileInputStream");
        return responseBody;
    }

    HttpResponseBody httpGetFile(String api, Object object) {
        String url = getUrl() + api;
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String requestBody = null;
        if (null != object) {
            requestBody = JSON.toJSONString(object);
        }
        HttpResponseBody httpResponseBody = HttpClientHelper.httpGetStream(url, headerMap, requestBody);
        addLog(url, "POST", headerMap, requestBody, requestTime, "FileInputStream");
        return httpResponseBody;
    }

    String httpPostMuFile(String api, Object object, MultipartFile file, String... pathVariable) {
        String url = getUrl() + api;
        url = pathVariable(url, pathVariable);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String requestBody = JSON.toJSONString(object);
        HttpResponseBody responseBody = HttpClientHelper.httpPost(url, requestBody, file, headerMap);
        addLog(url, "PUT", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    String httpPutMuFile(String api, Object object, MultipartFile file, String... pathVariable) {
        String url = getUrl() + api;
        url = pathVariable(url, pathVariable);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String requestBody = JSON.toJSONString(object);
        HttpResponseBody responseBody = HttpClientHelper.httpPut(url, requestBody, file, headerMap);
        addLog(url, "PUT", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    String httpGet(String api, Object object, String... pathVariable) {
        String url = getUrl() + api;
        url = pathVariable(url, pathVariable);
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        HttpResponseBody responseBody = HttpClientHelper.httpGet(url, requestBody, headerMap);
        addLog(url, "GET", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    String pathVariable(String url, String... pathVariable) {
        return url + ((pathVariable != null) ? ("/" + Arrays.stream(pathVariable).filter(Objects::nonNull).collect(Collectors.joining("/"))) : "");
    }

    void addLog(String url, String method, Map<String, String> headerMap, String requestBody, Date requestTime, String responseBody) {
        Date responseTime = new Date();
        HtpRequestLog log = new HtpRequestLog();
        log.setTraceId(MDC.get("TID"));
        log.setRequestUri(url);
        log.setRequestMethod(method);
        log.setRequestHeader(JSON.toJSONString(headerMap));
        log.setRequestBody(requestBody);
        log.setRequestTime(requestTime);
        log.setResponseBody(responseBody);
        log.setResponseTime(responseTime);
        EventUtil.publishEvent(new RequestLogEvent(log));
    }

}
