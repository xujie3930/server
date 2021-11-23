package com.szmsd.auth.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        Authentication authentication = event.getAuthentication();
        Object principal = authentication.getPrincipal();
        String username = "";
        if (principal instanceof User) {
            User u = (User) principal;
            username = u.getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        AuthenticationException exception = event.getException();
        this.logger.error("用户登录失败，username:{}", username, exception);
    }
}
