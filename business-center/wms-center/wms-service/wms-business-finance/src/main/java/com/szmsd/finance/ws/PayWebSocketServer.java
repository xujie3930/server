package com.szmsd.finance.ws;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.dto.PayMessageDTO;
import com.szmsd.finance.enums.PayScoketEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/pay/result/{orderNo}")
public class PayWebSocketServer {

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static final ConcurrentHashMap<String, PayWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收orderNo
     */
    private final String orderNo = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "orderNo") String orderNo) {
        //加入map中
        this.session = session;
        webSocketMap.put(orderNo, this);
        try {
            sendMessage(orderNo, PayScoketEnum.PAY_CONNECT.name());
            session.setMaxIdleTimeout(30000);
        } catch (IOException e) {
            log.error("订单号:" + orderNo + ",网络异常!!!!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam(value = "orderNo") String orderNo) {
        //从map中删除
        webSocketMap.remove(orderNo);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("报文:" + message);

        if(StringUtils.isEmpty(message)){

            throw new RuntimeException("onmessage null");
        }

        try {
            //TODO 校验 Authorization
            PayMessageDTO payMessageDTO = JSON.parseObject(message, PayMessageDTO.class);
            String authorization = payMessageDTO.getAuthorization();

            if(StringUtils.isNotEmpty(authorization)){
                session.setMaxIdleTimeout(30000);
            }

            sendMessage(payMessageDTO.getOrderNo(), payMessageDTO.getPayStatus().name());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * @param session session
     * @param error   error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:" + this.orderNo + ",原因:" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     *
     * @param orderNo orderNo
     * @param status  status
     * @throws IOException e
     */
    public static void sendMessage(String orderNo, String status) throws IOException {
        PayWebSocketServer webSocketServer = webSocketMap.get(orderNo);
        if (webSocketServer == null) {
            log.error("用户：{} 未建立ws连接", orderNo);
            return;
        }
        Session session = webSocketServer.session;
        session.getBasicRemote().sendText(status);
    }
}
