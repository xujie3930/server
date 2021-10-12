package com.szmsd.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysOauthClientDetails implements Serializable {

    public String clientId;

    private String webServerRedirectUri;
}
