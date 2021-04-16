package com.szmsd.http.service.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.annotation.LogIgnore;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.config.inner.DefaultApiConfig;
import com.szmsd.http.config.inner.UrlGroupConfig;
import com.szmsd.http.config.inner.api.ApiConfig;
import com.szmsd.http.config.inner.url.UrlApiConfig;
import com.szmsd.http.config.inner.url.UrlConfig;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.domain.HtpUrlGroup;
import com.szmsd.http.enums.HttpUrlType;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import com.szmsd.http.service.IHtpConfigService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 15:03
 */
abstract class AbstractRequest {

    @Resource
    private IHtpConfigService iHtpConfigService;

    protected HttpConfig httpConfig;

    public AbstractRequest(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    abstract HttpUrlType getHttpUrlType();

    String getUrlGroupName(String warehouseCode) {
        // 兼容处理，没有仓库编码调用默认映射规则
        String urlGroupName = null;
        if (this.isNotEmpty(warehouseCode)) {
            String matchWarehouseGroupName = null;
            Map<String, Set<String>> warehouseGroupMap = this.httpConfig.getWarehouseGroup();
            if (null != warehouseGroupMap) {
                Set<String> warehouseGroupNameSet = warehouseGroupMap.keySet();
                for (String warehouseGroupName : warehouseGroupNameSet) {
                    if (warehouseGroupMap.get(warehouseGroupName).contains(warehouseCode)) {
                        matchWarehouseGroupName = warehouseGroupName;
                        break;
                    }
                }
            }
            if (this.isNotEmpty(matchWarehouseGroupName)) {
                urlGroupName = this.httpConfig.getMapperGroup().get(matchWarehouseGroupName);
            }
        }
        if (this.isEmpty(urlGroupName) && this.isEmpty(warehouseCode)) {
            // 获取默认映射
            urlGroupName = this.httpConfig.getDefaultUrlGroup();
        }
        if (this.isEmpty(urlGroupName)) {
            throw new CommonException("999", "仓库编码[" + warehouseCode + "]未配置映射规则");
        }
        return urlGroupName;
    }

    UrlConfig getUrlConfig(String urlGroupName) {
        UrlGroupConfig urlGroupConfig = this.httpConfig.getUrlGroup().get(urlGroupName);
        if (null == urlGroupConfig) {
            throw new CommonException("999", "url group config cant be null");
        }
        HttpUrlType httpUrlType = this.getHttpUrlType();
        if (null == httpUrlType) {
            throw new CommonException("999", "http url type cant be null");
        }
        UrlConfig urlConfig = null;
        switch (httpUrlType) {
            case WMS:
                urlConfig = urlGroupConfig.getWms();
                break;
            case THIRD_PAYMENT:
                urlConfig = urlGroupConfig.getThirdPayment();
                break;
            case PRICED_PRODUCT:
                urlConfig = urlGroupConfig.getPricedProduct();
                break;
            case CARRIER_SERVICE:
                urlConfig = urlGroupConfig.getCarrierService();
                break;
            case PRODUCT_REMOTE_AREA:
                urlConfig = urlGroupConfig.getProductRemoteArea();
                break;
        }
        if (null == urlConfig) {
            HtpUrlGroup htpUrlGroup = iHtpConfigService.selectHtpUrlGroup(urlGroupName);
            throw new CommonException("999", htpUrlGroup.getGroupName() + "的[" + HttpUrlType.valueOf(httpUrlType.name()).getKey() + "]服务未配置");
        }
        return urlConfig;
    }

    ApiConfig getApiConfig(UrlConfig urlConfig) {
        ApiConfig apiConfig = null;
        if (urlConfig instanceof UrlApiConfig) {
            apiConfig = ((UrlApiConfig) urlConfig).getApi();
        }
        if (null == apiConfig) {
            DefaultApiConfig defaultApiConfig = this.httpConfig.getDefaultApiConfig();
            HttpUrlType httpUrlType = this.getHttpUrlType();
            switch (httpUrlType) {
                case WMS:
                    return defaultApiConfig.getWms();
                case THIRD_PAYMENT:
                    return defaultApiConfig.getThirdPayment();
                case PRICED_PRODUCT:
                    return defaultApiConfig.getPricedProduct();
                case CARRIER_SERVICE:
                    return defaultApiConfig.getCarrierService();
                case PRODUCT_REMOTE_AREA:
                    return defaultApiConfig.getProductRemoteArea();
            }
        }
        return apiConfig;
    }

    String getApi(UrlConfig urlConfig, String api) {
        ApiConfig apiConfig = this.getApiConfig(urlConfig);
        if (null == apiConfig) {
            throw new CommonException("999", "api config is null");
        }
        // api
        // base-info.seller         ->>> baseInfo.seller
        // base-info.shipment-rule  ->>> baseInfo.shipmentRule
        // exception.processing     ->>> exception.processing
        if (api.contains("-")) {
            String[] strs = api.split("-");
            StringBuilder builder = new StringBuilder(strs[0]);
            for (int i = 1; i < strs.length; i++) {
                String str = strs[i];
                builder.append(str.substring(0, 1).toUpperCase())
                        .append(str.substring(1));
            }
            api = builder.toString();
        }
        String s = Utils.get(apiConfig, api);
        if (this.isEmpty(s)) {
            throw new CommonException("999", "api is null");
        }
        return s;
    }

    HttpResponseBody httpRequestBody(String warehouseCode, String api, Object object, HttpMethod httpMethod, Object... pathVariable) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        if (url.contains("{")) {
            url = MessageFormat.format(url, pathVariable);
        }
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = urlConfig.getHeaders();
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
        if (null != object && object.getClass().isAnnotationPresent(LogIgnore.class)) {
            LogIgnore logIgnore = object.getClass().getAnnotation(LogIgnore.class);
            SimplePropertyPreFilter filter = new SimplePropertyPreFilter();
            filter.getExcludes().addAll(Arrays.asList(logIgnore.value()));
            logRequestBody = JSON.toJSONString(object, filter);
        } else {
            logRequestBody = requestBody;
        }
        this.addLog(warehouseCode, urlGroupName, url, httpMethod.name(), headerMap, logRequestBody, requestTime, responseBody.getBody());
        return responseBody;
    }

    protected String httpRequest(String warehouseCode, String api, Object object, HttpMethod httpMethod, Object... pathVariable) {
        return this.httpRequestBody(warehouseCode, api, object, httpMethod, pathVariable).getBody();
    }

    protected String httpPost(String warehouseCode, String api, Object object) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.POST);
    }

    protected String httpPut(String warehouseCode, String api, Object object) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.PUT);
    }

    protected String httpDelete(String warehouseCode, String api, Object object) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.DELETE);
    }

    protected String httpPost(String warehouseCode, String api, Object object, Object... pathVariable) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.POST, pathVariable);
    }

    protected String httpPut(String warehouseCode, String api, Object object, Object... pathVariable) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.PUT, pathVariable);
    }

    protected String httpDelete(String warehouseCode, String api, Object object, Object... pathVariable) {
        return this.httpRequest(warehouseCode, api, object, HttpMethod.DELETE, pathVariable);
    }

    protected HttpResponseBody httpPostBody(String warehouseCode, String api, Object object) {
        return this.httpRequestBody(warehouseCode, api, object, HttpMethod.POST);
    }

    protected HttpResponseBody httpPutBody(String warehouseCode, String api, Object object) {
        return this.httpRequestBody(warehouseCode, api, object, HttpMethod.PUT);
    }

    protected HttpResponseBody httpDeleteBody(String warehouseCode, String api, Object object) {
        return this.httpRequestBody(warehouseCode, api, object, HttpMethod.DELETE);
    }

    protected FileStream httpPostFile(String warehouseCode, String api, Object object) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        Map<String, String> headerMap = urlConfig.getHeaders();
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
        this.addLog(warehouseCode, urlGroupName, url, "POST", headerMap, requestBody, requestTime, "FileInputStream");
        return responseBody;
    }

    protected HttpResponseBody httpGetFile(String warehouseCode, String api, Object object, Object... pathVariable) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        if (url.contains("{")) {
            url = MessageFormat.format(url, pathVariable);
        }
        Map<String, String> headerMap = urlConfig.getHeaders();
        Date requestTime = new Date();
        String requestBody = null;
        if (null != object) {
            requestBody = JSON.toJSONString(object);
        }
        HttpResponseBody httpResponseBody = HttpClientHelper.httpGetStream(url, headerMap, requestBody);
        this.addLog(warehouseCode, urlGroupName, url, "POST", headerMap, requestBody, requestTime, "FileInputStream");
        return httpResponseBody;
    }

    protected String httpPostMuFile(String warehouseCode, String api, Object object, MultipartFile file, Object... pathVariable) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        if (url.contains("{")) {
            url = MessageFormat.format(url, pathVariable);
        }
        Map<String, String> headerMap = urlConfig.getHeaders();
        Date requestTime = new Date();
        String requestBody = JSON.toJSONString(object);
        HttpResponseBody responseBody = HttpClientHelper.httpPost(url, requestBody, file, headerMap);
        this.addLog(warehouseCode, urlGroupName, url, "PUT", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    protected String httpPutMuFile(String warehouseCode, String api, Object object, MultipartFile file, Object... pathVariable) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        if (url.contains("{")) {
            url = MessageFormat.format(url, pathVariable);
        }
        Map<String, String> headerMap = urlConfig.getHeaders();
        Date requestTime = new Date();
        String requestBody = JSON.toJSONString(object);
        HttpResponseBody responseBody = HttpClientHelper.httpPut(url, requestBody, file, headerMap);
        this.addLog(warehouseCode, urlGroupName, url, "PUT", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    protected String httpGet(String warehouseCode, String api, Object object, Object... pathVariable) {
        String urlGroupName = this.getUrlGroupName(warehouseCode);
        UrlConfig urlConfig = this.getUrlConfig(urlGroupName);
        String url = urlConfig.getUrl() + this.getApi(urlConfig, api);
        if (url.contains("{")) {
            url = MessageFormat.format(url, pathVariable);
        }
        Map<String, String> headerMap = urlConfig.getHeaders();
        String requestBody = JSON.toJSONString(object);
        Date requestTime = new Date();
        HttpResponseBody responseBody = HttpClientHelper.httpGet(url, requestBody, headerMap);
        this.addLog(warehouseCode, urlGroupName, url, "GET", headerMap, requestBody, requestTime, responseBody.getBody());
        return responseBody.getBody();
    }

    void addLog(String warehouseCode, String urlGroup, String url, String method, Map<String, String> headerMap, String requestBody, Date requestTime, String responseBody) {
        Date responseTime = new Date();
        HtpRequestLog log = new HtpRequestLog();
        log.setTraceId(MDC.get("TID"));
        log.setWarehouseCode(warehouseCode);
        log.setRequestUrlGroup(urlGroup);
        log.setRequestUri(url);
        log.setRequestMethod(method);
        log.setRequestHeader(JSON.toJSONString(headerMap));
        log.setRequestBody(requestBody);
        log.setRequestTime(requestTime);
        log.setResponseBody(responseBody);
        log.setResponseTime(responseTime);
        EventUtil.publishEvent(new RequestLogEvent(log));
    }

    boolean isNotEmpty(String str) {
        return !this.isEmpty(str);
    }

    boolean isEmpty(String str) {
        return null == str || "".equals(str.trim());
    }
}
