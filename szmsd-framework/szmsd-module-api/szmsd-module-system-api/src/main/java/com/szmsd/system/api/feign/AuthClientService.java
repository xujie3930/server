package com.szmsd.system.api.feign;

import com.szmsd.system.api.factory.AuthClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "AuthClientService", name = "szmsd-auth", fallbackFactory = AuthClientFallbackFactory.class)
public interface AuthClientService {

    @RequestMapping(value = "/oauth/token", method = RequestMethod.POST) //目录注意一定要能匹配上
    Object token(    //下面是方法实例，从controller拷贝过来的
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam("user_type") String user_type,
                        @RequestParam("client_id") String client_id,
                        @RequestParam("grant_type") String grant_type,
                        @RequestParam("client_secret") String client_secret,

                        @RequestParam("LOGIN_FREE") String LOGIN_FREE

                        );

}
