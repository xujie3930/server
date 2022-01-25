package com.szmsd.http.plugins;

import lombok.Data;

@Data
public class TokenValue {

    private String accessToken;

    private Long expiresIn;

    private String scope;

    private String refreshToken;
}
