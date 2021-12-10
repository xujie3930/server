package com.szmsd.http.controller;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import com.szmsd.http.vo.HttpResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"HTTP调用接口"})
@ApiSort(10000)
@RestController
@RequestMapping("/api/rmi")
public class RemoteInterfaceController extends BaseController {

    @PostMapping
    @ApiOperation(value = "HTTP调用接口 - #1", position = 100)
    @ApiImplicitParam(name = "dto", value = "dto", dataType = "HttpRequestDto")
    public R<HttpResponseVO> rmi(@RequestBody @Validated HttpRequestDto dto) {
        // 暂时只支持
        HttpResponseVO responseVO = new HttpResponseVO();
        Date requestTime = new Date();
        String uri = dto.getUri();
        String requestBody = JSON.toJSONString(dto.getBody());
        try {
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
            HttpResponseBody httpResponseBody = HttpClientHelper.executeOnByteArray(request, requestBody, dto.getHeaders());
            if (httpResponseBody instanceof HttpResponseBody.HttpResponseByteArrayWrapper) {
                HttpResponseBody.HttpResponseByteArrayWrapper byteArrayWrapper = (HttpResponseBody.HttpResponseByteArrayWrapper) httpResponseBody;
                responseVO.setStatus(byteArrayWrapper.getStatus());
                responseVO.setHeaders(headerToMap(byteArrayWrapper.getHeaders()));
                responseVO.setBody(byteArrayWrapper.getByteArray());
            } else if (httpResponseBody instanceof HttpResponseBody.HttpResponseBodyEmpty) {
                responseVO.setStatus(0);
                responseVO.setBody("请求失败".getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            responseVO.setStatus(0);
            responseVO.setBody(("请求失败，" + e.getMessage()).getBytes(StandardCharsets.UTF_8));
        } finally {
            Date responseTime = new Date();
            HtpRequestLog requestLog = new HtpRequestLog();
            requestLog.setRemark("" + responseVO.getStatus());
            requestLog.setTraceId(MDC.get("TID"));
            requestLog.setRequestUri(uri);
            requestLog.setRequestMethod(dto.getMethod().name());
            requestLog.setRequestHeader(JSON.toJSONString(dto.getHeaders()));
            requestLog.setRequestBody(requestBody);
            requestLog.setRequestTime(requestTime);
            requestLog.setResponseHeader(JSON.toJSONString(responseVO.getHeaders()));
            String responseBody = new String(responseVO.getBody(), StandardCharsets.UTF_8);
            requestLog.setResponseBody(responseBody);
            requestLog.setResponseTime(responseTime);
            EventUtil.publishEvent(new RequestLogEvent(requestLog));
        }
        return R.ok(responseVO);
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
}
