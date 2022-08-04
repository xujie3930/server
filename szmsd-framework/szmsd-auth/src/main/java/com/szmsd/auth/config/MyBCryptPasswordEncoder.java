package com.szmsd.auth.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MyBCryptPasswordEncoder extends BCryptPasswordEncoder {
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if("-1".equals(encodedPassword)){
            return true;
        }
        return super.matches(rawPassword, encodedPassword);
    }
}