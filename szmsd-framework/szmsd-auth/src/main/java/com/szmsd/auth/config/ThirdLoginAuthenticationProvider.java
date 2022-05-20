package com.szmsd.auth.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class ThirdLoginAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ThirdLoginAuthenticationToken thirdLoginAuthenticationToken = (ThirdLoginAuthenticationToken) authentication;
        thirdLoginAuthenticationToken.setAuthenticated(true);
        return thirdLoginAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ThirdLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
