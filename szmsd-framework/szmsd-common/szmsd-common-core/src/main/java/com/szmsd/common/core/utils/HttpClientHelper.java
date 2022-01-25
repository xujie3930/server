package com.szmsd.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

/**
 * @author Gao Junwen
 * @version 1.0
 * @Description HTTP请求(GET / POST)工具
 * @Created on 2020年8月28日
 * @since JDK1.8
 */
public class HttpClientHelper {

    private static final Logger log = LoggerFactory.getLogger(HttpClientHelper.class);

    private final CloseableHttpClient httpClient;

    private HttpClientHelper() {
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (null == sc) {
            throw new RuntimeException("init SSLContext error");
        }
        //注册访问协议相关的Socket工厂
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                // SSLConnectionSocketFactory.getSystemSocketFactory();
                .register("https", new SSLConnectionSocketFactory(sc)).build();
        //HttpConnection工厂：皮遏制写请求/解析响应处理器
        HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory = new
                ManagedHttpClientConnectionFactory(DefaultHttpRequestWriterFactory.INSTANCE,
                DefaultHttpResponseParserFactory.INSTANCE);
        //DNS解析器
        DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;

        //创建池化连接管理器
        PoolingHttpClientConnectionManager poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);
        //默认为Socket配置
        SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
        poolConnManager.setDefaultSocketConfig(defaultSocketConfig);

        // 设置整个连接池的最大连接数
        poolConnManager.setMaxTotal(1000);
        // 每个路由的默认最大连接，每个路由实际最大连接默认为DefaultMaxPerRoute控制，maxTotal是整个池子最大数
        // DefaultMaxPerRoute设置过小无法支持大并发（ConnectPoolTimeoutException: Timeout waiting for connect from pool) 路由是maxTotal的细分
        //每个路由最大连接数
        poolConnManager.setDefaultMaxPerRoute(1000);
        //在从连接池获取连接时，连接不活跃多长时间后需要一次验证，默认2S
        poolConnManager.setValidateAfterInactivity(5 * 1000);

        //默认请求配置
        RequestConfig requestConfig = RequestConfig.custom()
                //设置连接超时时间
                .setConnectTimeout(600 * 1000)
                //设置等待数据超时时间
                .setSocketTimeout(600 * 1000)
                //设置从连接池获取连接的等待超时时间
                .setConnectionRequestTimeout(20000)
                .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setConnectionManager(poolConnManager)
                //设置连接池不是共享模式
                .setConnectionManagerShared(false)
                //定期回调空闲连接
                .evictIdleConnections(60, TimeUnit.SECONDS)
                //定期回收过期
                .evictExpiredConnections()
                //连接存活时间，如果不设置，根据长连接信息决定
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                //设置默认请求配置
                .setDefaultRequestConfig(requestConfig)
                // 连接重试策略，是否能keepalive
                .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                //长连接配置，即获取长连接生产多少时间
                .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                //设置重试次数，默认是3次，启用重试
                .setRetryHandler(new DefaultHttpRequestRetryHandler());

        httpClient = httpClientBuilder.build();

        //JVM停止或重启时，关闭连接池释放连接
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    log.info(e.getMessage());
                }
            }
        });
    }

    public static synchronized CloseableHttpClient getHttpClient() {
        return HttpClientHelperInstance.INSTANCE.httpClient;
    }

    /**
     * 执行 http post 请求，这里不支持表单的请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static HttpResponseBody httpPost(String url, String requestBody, Map<String, String> headerMap) {
        return execute(new HttpPost(url), requestBody, headerMap);
    }

    /**
     * 执行 http post 请求，这里支持表单的请求
     *
     * @param url           请求URL
     * @param requestParams 请求参数
     * @param headerMap     请求Header
     * @return 响应Body
     */
    public static HttpResponseBody httpPost(String url, Map<String, Object> requestParams, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            String result = null;
            //添加http头信息
            setHeader(httpPost, headerMap);
            List<NameValuePair> paramList = new ArrayList<>();
            for (String key : requestParams.keySet()) {
                paramList.add(new BasicNameValuePair(key, String.valueOf(requestParams.get(key))));
            }
            HttpEntity httpEntity = new UrlEncodedFormEntity(paramList, "UTF-8");
            httpPost.setEntity(httpEntity);
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            } else {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            }
            return new HttpResponseBody.HttpResponseBodyWrapper(status, result);
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return new HttpResponseBody.HttpResponseBodyEmpty();
    }

    /**
     * 执行 http put 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static HttpResponseBody httpPut(String url, String requestBody, Map<String, String> headerMap) {
        return execute(new HttpPut(url), requestBody, headerMap);
    }

    /**
     * 执行 http delete 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static HttpResponseBody httpDelete(String url, String requestBody, Map<String, String> headerMap) {
        return execute(new HttpDelete(url), requestBody, headerMap);
    }

    /**
     * 执行 http get 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static HttpResponseBody httpGet(String url, String requestBody, Map<String, String> headerMap) {
        // get参数兼容
        if (null != requestBody) {
            StringBuilder paramsBuilder = new StringBuilder();
            if (url.lastIndexOf("?") == -1) {
                paramsBuilder.append("?");
            }
            JSONObject jsonObject = JSON.parseObject(requestBody);
            if (null != jsonObject) {
                for (String key : jsonObject.keySet()) {
                    try {
                        paramsBuilder.append(key)
                                .append("=")
                                .append(URLEncoder.encode(jsonObject.getString(key), "utf-8"))
                                .append("&");
                    } catch (UnsupportedEncodingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
                paramsBuilder.deleteCharAt(paramsBuilder.length() - 1);
                url = url + paramsBuilder.toString();
            }
        }
        return execute(new HttpGet(url), requestBody, headerMap);
    }

    public static String builderGetParams(String requestBody) {
        // get参数兼容
        if (null != requestBody) {
            StringJoiner joiner = new StringJoiner("&");
            JSONObject jsonObject = JSON.parseObject(requestBody);
            if (null != jsonObject) {
                for (String key : jsonObject.keySet()) {
                    try {
                        joiner.add(key + "=" + URLEncoder.encode(jsonObject.getString(key), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            return joiner.toString();
        }
        return "";
    }

    public static HttpResponseBody execute(HttpEntityEnclosingRequestBase request, String requestBody, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            String result = null;
            //添加http头信息
            setHeader(request, headerMap);
            setRaw(request, requestBody);
            response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            } else {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "UTF-8");
                }
            }
            return new HttpResponseBody.HttpResponseBodyWrapper(status, result);
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return new HttpResponseBody.HttpResponseBodyEmpty();
    }

    public static HttpResponseBody executeOnByteArray(HttpEntityEnclosingRequestBase request, String requestBody, Map<String, String> headerMap) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            byte[] result = null;
            //添加http头信息
            setHeader(request, headerMap);
            setRaw(request, requestBody);
            response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toByteArray(entity);
            }
            Header[] headers = response.getAllHeaders();
            return new HttpResponseBody.HttpResponseByteArrayWrapper(status, headers, result);
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
            return new HttpResponseBody.HttpResponseBodyEmpty(e.getMessage());
        }
    }

    public static HttpResponseBody httpPostStream(String url, Map<String, String> headerMap, String requestBody) {
        return execute(new HttpPost(url), headerMap, requestBody);
    }

    public static HttpResponseBody httpGetStream(String url, Map<String, String> headerMap, String requestBody) {
        return execute(new HttpGet(url), headerMap, requestBody);
    }

    public static HttpResponseBody execute(HttpEntityEnclosingRequestBase request, Map<String, String> headerMap, String requestBody) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            //添加http头信息
            setHeader(request, headerMap);
            setRaw(request, requestBody);
            response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            Header[] headers = null;
            byte[] byteArray = null;
            if (entity != null) {
                headers = response.getAllHeaders();
                byteArray = IOUtils.toByteArray(entity.getContent());
            }
            return new HttpResponseBody.HttpResponseByteArrayWrapper(status, headers, byteArray);
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return new HttpResponseBody.HttpResponseBodyEmpty();
    }

    /**
     * 执行 http put 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param file        文件
     * @param headerMap   请求Header
     * @return
     */
    public static HttpResponseBody httpPut(String url, String requestBody, MultipartFile file, Map<String, String> headerMap) {
        return execute(new HttpPut(url), headerMap, requestBody, file);
    }

    /**
     * 执行 http post 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param file        文件
     * @param headerMap   请求Header
     * @return
     */
    public static HttpResponseBody httpPost(String url, String requestBody, MultipartFile file, Map<String, String> headerMap) {
        return execute(new HttpPost(url), headerMap, requestBody, file);
    }

    public static HttpResponseBody execute(HttpEntityEnclosingRequestBase request, Map<String, String> headerMap, String requestBody, MultipartFile file) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            String result = null;
            //添加http头信息
            setHeader(request, headerMap);
            setRaw(request, requestBody, file);
            response = httpClient.execute(request);
            int status = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            return new HttpResponseBody.HttpResponseBodyWrapper(status, result);
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return new HttpResponseBody.HttpResponseBodyEmpty();
    }

    public static void setHeader(AbstractHttpMessage httpMessage, Map<String, String> headerMap) {
        if (null == headerMap) {
            return;
        }
        //添加http头信息
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpMessage.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    public static void setRaw(HttpEntityEnclosingRequestBase httpEntity, String requestBody) {
        if (null == requestBody) {
            return;
        }
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestBody.getBytes(StandardCharsets.UTF_8));
        byteArrayEntity.setContentType("application/json;charset=UTF-8");
        httpEntity.setEntity(byteArrayEntity);
    }

    public static void setRaw(HttpEntityEnclosingRequestBase httpEntity, String requestBody, MultipartFile file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            String originalFilename = file.getOriginalFilename();
            builder.addBinaryBody(file.getName(), file.getInputStream(), ContentType.MULTIPART_FORM_DATA, originalFilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, String> params = JSON.parseObject(requestBody, Map.class);
        if (params != null) {
            for (String key : params.keySet()) {
                builder.addPart(key, new StringBody(params.get(key), ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
        HttpEntity entity = builder.build();
        httpEntity.setEntity(entity);
    }

    private static class HttpClientHelperInstance {
        private static final HttpClientHelper INSTANCE = new HttpClientHelper();
    }

    public static class HttpDelete extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "DELETE";

        public HttpDelete() {
        }

        public HttpDelete(URI uri) {
            this.setURI(uri);
        }

        public HttpDelete(String uri) {
            this.setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

    public static class HttpGet extends HttpEntityEnclosingRequestBase {
        public static final String METHOD_NAME = "GET";

        public HttpGet() {
        }

        public HttpGet(URI uri) {
            this.setURI(uri);
        }

        public HttpGet(String uri) {
            this.setURI(URI.create(uri));
        }

        @Override
        public String getMethod() {
            return METHOD_NAME;
        }
    }

}
