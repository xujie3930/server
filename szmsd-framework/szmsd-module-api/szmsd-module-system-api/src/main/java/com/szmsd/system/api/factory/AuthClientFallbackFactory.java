package com.szmsd.system.api.factory;

import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.system.api.feign.AuthClientService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务降级处理
 *
 * @author szmsd
 */
@Component
public class AuthClientFallbackFactory implements FallbackFactory<AuthClientService> {
    private static final Logger log = LoggerFactory.getLogger(AuthClientFallbackFactory.class);

    @Override
    public AuthClientService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new AuthClientService() {


            @Override
            public Object token(String username, String password, String user_type, String client_id, String grant_type, String client_secret, String LOGIN_FREE) {
                throw new CommonException("999", ExceptionMessageEnum.FAIL.getValue());
            }



        };
    }
}
