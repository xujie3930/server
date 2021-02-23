package com.szmsd.websocket.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.websocket.api.WebsocketService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class WebsocketFeignFactory  implements FallbackFactory<WebsocketService> {


    @Override
    public WebsocketService create(Throwable throwable) {
        return new WebsocketService() {
            @Override
            public R pushToWeb(String message, String toUserId) {
                return R.failed();
            }
        };
    }
}
