package com.szmsd.doc.utils;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.doc.config.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class AuthenticationUtil {

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static LoginUser getUser() {
        Authentication authentication = getAuthentication();
        if (null != authentication) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
        }
        return null;
    }

    public static String getSellerCode(){
        return Optional.ofNullable(getUser()).map(LoginUser::getSellerCode).orElseThrow(()->new CommonException("500","用户信息异常"));
    }
}
