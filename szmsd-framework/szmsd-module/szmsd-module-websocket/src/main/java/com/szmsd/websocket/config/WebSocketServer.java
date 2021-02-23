package com.szmsd.websocket.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bil.api.feign.BilScanService;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @FileName WebSocketServer.java
 * @Description WebSocket核心服务
 * @Date 2020-07-21 10:50
 * @Author Yan Hua
 * @Version 1.0
 */
@ServerEndpoint("/imserver/{userId}/{userName}/{siteCode}/{siteName}")
@Component
public class WebSocketServer {
    static Log log = LogFactory.get(WebSocketServer.class);
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收userId
     */
    private String userId = null;
    private String userName= null;

    /**
     *
     * 网点
     */
    private String siteCode = null;
    private String siteName = null;

    public static String siteId = RandomUtil.randomNumbers(6);
    /**
     * 连接建立成功调用的方法
     */
    //此处是解决无法注入的关键
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServer.applicationContext = applicationContext;
    }

    private BilScanService bilScanService;

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId,
                       @PathParam("userName") String userName,
                       @PathParam("siteCode") String siteCode,
                       @PathParam("siteName") String siteName) {

        this.session = session;
        this.userId = userId;
        this.userName = userName;
        this.userId = userId;
        this.siteName = siteName;

        this.siteCode=siteCode;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            // 加入set中
        } else {
            webSocketMap.put(userId, this);
            // 加入set中
            addOnlineCount();
            // 在线数加1
            // 新增网点 + 网点信息
            // ---------------业务代码-----------------
        }

        log.info("用户连接:" + userId+":"+userName + ",用户所在的网点是"+siteCode+"："+siteName+",当前在线人数为:" + getOnlineCount());
        bilScanService=getApplicationContext().getBean(BilScanService.class);
        try {
            sendMessage("连接成功");


        } catch (IOException e) {
            log.error("用户:" + userId + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            // 从set中删除
            subOnlineCount();
            // 删除网点 + 网点记录
            // ---------------业务代码-----------------
        }
        log.info("用户退出:" + userId + ",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + userId + ",报文:" + message);
        //可以群发消息
        //消息保存到数据库、redis
        if (StringUtils.isNotBlank(message)) {
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId", this.userId);
                String toUserId = jsonObject.getString("toUserId");
                //String contentText=jsonObject.getString("contentText");
                //传送给对应toUserId用户的websocket
                if (StringUtils.isNotBlank(toUserId) && webSocketMap.containsKey(toUserId)) {
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                } else {
                    log.error("请求的userId:" + toUserId + "不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                    // 新增 消息推送 记录
                    // ---------------业务代码-----------------
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.userId + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) {
        try {
            log.info("发送消息到:" + userId + "，报文:" + message);
            if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
                webSocketMap.get(userId).sendMessage(message);
            } else {
                log.error("用户" + userId + ",不在线！");
            }
        } catch (Exception e) {
            log.error("sendInfo:", e);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
