package com.szmsd.websocket.api;


import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.websocket.factory.WebsocketFeignFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



/**
 *
 */
@FeignClient(contextId = "websocketservice", value = ServiceNameConstants.WEBSOCKET_SERVICE,fallbackFactory =WebsocketFeignFactory.class )
public interface WebsocketService
{
    @PostMapping(value = "webSocket/push",consumes = "application/json")
    public R pushToWeb(@RequestParam(name = "message")String message, @RequestParam(name = "toUserId") String toUserId) ;
}
