package com.szmsd.common.core.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
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
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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
        //注册访问协议相关的Socket工厂
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSystemSocketFactory()).build();

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
                .setConnectTimeout(2 * 10000)
                //设置等待数据超时时间
                .setSocketTimeout(5 * 10000)
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
     * 执行 http post 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static String httpPost(String url, String requestBody, Map<String, String> headerMap) {
        return execute(new HttpPost(url), requestBody, headerMap);
    }

    /**
     * 执行 http put 请求
     *
     * @param url         请求URL
     * @param requestBody 请求Body
     * @param headerMap   请求Header
     * @return 响应Body
     */
    public static String httpPut(String url, String requestBody, Map<String, String> headerMap) {
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
    public static String httpDelete(String url, String requestBody, Map<String, String> headerMap) {
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
    public static String httpGet(String url, String requestBody, Map<String, String> headerMap) {
        return execute(new HttpGet(url), requestBody, headerMap);
    }

    public static String execute(HttpEntityEnclosingRequestBase request, String requestBody, Map<String, String> headerMap) {
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
            return result;
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static FileStream httpPostStream(String url, Map<String, String> headerMap, String requestBody) {
        return execute(new HttpPost(url), headerMap, requestBody);
    }

    public static FileStream execute(HttpEntityEnclosingRequestBase request, Map<String, String> headerMap, String requestBody) {
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            //添加http头信息
            setHeader(request, headerMap);
            setRaw(request, requestBody);
            response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return new FileStream().setInputStream(IOUtils.toByteArray(entity.getContent())).setContentDisposition(response.getHeaders("Content-Disposition")[0].getValue());
            }
        } catch (Exception e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static void setHeader(AbstractHttpMessage httpMessage, Map<String, String> headerMap) {
        //添加http头信息
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpMessage.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    public static void setRaw(HttpEntityEnclosingRequestBase httpEntity, String requestBody) {
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(requestBody.getBytes(StandardCharsets.UTF_8));
        byteArrayEntity.setContentType("application/json;charset=UTF-8");
        httpEntity.setEntity(byteArrayEntity);
    }

    private static class HttpClientHelperInstance {
        private static final HttpClientHelper INSTANCE = new HttpClientHelper();
    }

    static class HttpDelete extends HttpEntityEnclosingRequestBase {
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

    static class HttpGet extends HttpEntityEnclosingRequestBase {
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
