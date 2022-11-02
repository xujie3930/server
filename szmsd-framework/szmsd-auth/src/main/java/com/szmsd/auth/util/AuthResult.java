package com.szmsd.auth.util;

import lombok.Data;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.Serializable;

@Data
public class AuthResult implements Serializable {

    private OAuth2AccessToken accessToken;

    private String matcher;

}
