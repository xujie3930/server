package com.szmsd.common.core.utils;

import com.google.common.hash.Hashing;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static Logger log = LoggerFactory.getLogger(HttpClientHelper.class);

    private static PoolingHttpClientConnectionManager poolConnManager = null;

    private static CloseableHttpClient httpClient = null;

    public static synchronized CloseableHttpClient getHttpClient() {
        if (null == httpClient) {
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
            poolConnManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);
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
                    //设置重试次数，默认是3次；当前是禁用
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

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
        return httpClient;
    }


    /**
     * 发送 GET 请求（HTTP），不带输入数据
     *
     * @param url
     * @return
     */
    public static String doGet(String url, Map<String, Object> header) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     *
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, String> params, Map<String, Object> header) {
        long a = System.currentTimeMillis();
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        HttpGet httpPost = null;
        try {
            httpPost = new HttpGet(apiUrl);
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                //不推荐使用CloseableHttpResponse.close关闭连接，他将直接关闭Socket，导致长连接不能复用
                EntityUtils.consume(response.getEntity());
            }

            StringBuilder logsb = new StringBuilder();
            logsb.append("##################\n");
            logsb.append("# 发送报文：" + params + "\n");
            logsb.append("# 响应代码：" + status + "\n");
            logsb.append("# 响应报文：" + result + "\n");
            logsb.append("# 耗时：" + (System.currentTimeMillis() - a) + "\n");
            logsb.append("########################################################################\n");
            log.info(logsb.toString());

            return result;

        } catch (IOException e) {
            try {
                if (null != response)
                    EntityUtils.consume(response.getEntity());
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
            log.error(e.getMessage(), e);
        }
        return result;
    }

    public static String doPost(String url) {
        return doPost(url, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url    接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String url, Map<String, Object> params) {
        long a = System.currentTimeMillis();
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                //不推荐使用CloseableHttpResponse.close关闭连接，他将直接关闭Socket，导致长连接不能复用
                EntityUtils.consume(response.getEntity());
            }

            StringBuilder logsb = new StringBuilder();
            logsb.append("##################\n");
            logsb.append("# 发送报文：" + getPlanText(params) + "\n");
            logsb.append("# 响应代码：" + status + "\n");
            logsb.append("# 响应报文：" + result + "\n");
            logsb.append("# 耗时：" + (System.currentTimeMillis() - a) + "\n");
            logsb.append("########################################################################\n");
            log.info(logsb.toString());

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
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     *
     * @param url    接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostStr(String url, Map<String, String> params, Map<String, Object> header) {
        long a = System.currentTimeMillis();
        String result = null;
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpClient = getHttpClient();
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, String> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue());
                pairList.add(pair);
            }
            //添加http头信息
            for (Map.Entry<String, Object> entry : header.entrySet()) {
                httpPost.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            } else {
                //不推荐使用CloseableHttpResponse.close关闭连接，他将直接关闭Socket，导致长连接不能复用
                EntityUtils.consume(response.getEntity());
            }

            StringBuilder logsb = new StringBuilder();
            logsb.append("##################\n");
            logsb.append("# 发送报文：" + params + "\n");
            logsb.append("# 响应代码：" + status + "\n");
            logsb.append("# 响应报文：" + result + "\n");
            logsb.append("# 耗时：" + (System.currentTimeMillis() - a) + "\n");
            logsb.append("########################################################################\n");
            log.info(logsb.toString());

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
        return result;
    }

    /**
     * 参数Map格式化
     *
     * @param map
     * @return
     */
    public static String getPlanText(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey()).
                    append("=").
                    append(entry.getValue())
                    .append("&");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static byte[] sendGetByteFordownLoad(String url) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        InputStream in = null;
        byte[] data = null;
        ByteArrayOutputStream output = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
            httpGet.setConfig(requestConfig);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            in = entity.getContent();
            long length = entity.getContentLength();
            if (length <= 0 && in == null) {
                return null;
            }


            byte[] buffer = new byte[4096];
            int n = 0;
            output = new ByteArrayOutputStream();
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }
            data = output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static String sign(String paramJson, String timestamp, String method, String appSecret, String appKey, String version) {
        try {
            StringBuilder makeUpBuilder = new StringBuilder().append(appSecret).append("app_key").append(appKey).append("method").append(method).append("param_json").append(paramJson).append("timestamp").append(timestamp).append("v").append(version).append(appSecret);
            System.out.println(makeUpBuilder.toString());
            return Hashing.md5().hashBytes(makeUpBuilder.toString().getBytes("utf-8")).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
