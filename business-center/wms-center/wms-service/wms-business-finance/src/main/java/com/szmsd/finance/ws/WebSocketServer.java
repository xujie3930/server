package com.szmsd.finance.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Component
@ServerEndpoint("/ws/rechargeCallback/result/{customCode}")
public class WebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收customCode
     */
    private final String customCode = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "customCode") String customCode) {
        //加入map中
        this.session = session;
        webSocketMap.put(customCode, this);
        try {
            sendMessage(customCode, "连接成功");
        } catch (IOException e) {
            log.error("用户:" + customCode + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "customCode") String customCode) {
        //从map中删除
        webSocketMap.remove(customCode);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:" + customCode + ",报文:" + message);
    }

    /**
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.customCode + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     *
     * @param customCode customCode
     * @param status     status
     * @throws IOException e
     */
    public void sendMessage(String customCode, String status) throws IOException {
        WebSocketServer webSocketServer = webSocketMap.get(customCode);
        if (webSocketServer == null) {
            log.error("用户：{} 未建立ws连接", customCode);
            return;
        }
        Session session = webSocketServer.session;
        session.getBasicRemote().sendText(status);
    }

}
