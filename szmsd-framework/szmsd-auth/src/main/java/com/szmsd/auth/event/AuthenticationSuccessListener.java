package com.szmsd.auth.event;

import com.szmsd.common.security.domain.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessListener.class);

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User u = (User) principal;
            if (principal instanceof LoginUser) {
                logger.info("系统用户登录成功，username:{}", u.getUsername());
            } else {
                logger.info("授权用户登录成功，username:{}", u.getUsername());
            }
        }
    }
}
