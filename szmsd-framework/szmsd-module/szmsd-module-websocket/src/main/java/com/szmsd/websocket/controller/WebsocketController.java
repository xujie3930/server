package com.szmsd.websocket.controller;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.websocket.config.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.xml.ws.RequestWrapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @FileName DemoController.java
 * @Description WebSocket测试
 * @Date 2020-07-21 11:00
 * @Author Yan Hua
 * @Version 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "webSocket")
public class WebsocketController {

    @GetMapping("index")
    public ResponseEntity<String> index(){
        return ResponseEntity.ok("请求成功");
    }

    @GetMapping("page")
    public ModelAndView page(){
        return new ModelAndView("websocket");
    }

    @PostMapping("/push")
    public R pushToWeb( @RequestParam(name = "message")String message, @RequestParam(name = "toUserId") String toUserId)  {
        WebSocketServer.sendInfo(message,toUserId);
        return R.ok("消息发送成功！");
    }



    @RequestMapping("/push/all")
    public  R pushAll() throws IOException {
        scheduledCheck();
        return  R.ok("消息发送成功！");
    }

    @Scheduled(cron="0/10 * * * * *")
    public void scheduledCheck() {
        //log.info("定时群发任务开始执行");
        //遍历当前站点所有用户
//        List<WsSite> wsSiteList = wsSiteRepository.findWsSiteUser(WebSocketServer.siteId);
//        wsSiteList.forEach(wsSite -> {
//            //遍历当前用户的所有信息
//            List<WsMessage> wsMessageList = wsMessageRepository.findWsMessages(wsSite.getUserId());
//            wsMessageList.forEach(wsMessage -> {
//                //发送信息
//                try {
//                    WebSocketServer.sendInfo(wsMessage.getMessage(),wsMessage.getToUserId());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            //清理发送的信息(更新siteId和状态)
//            wsMessageRepository.deleteWsMessages(WebSocketServer.siteId,wsSite.getUserId());
//        });
    }
}
