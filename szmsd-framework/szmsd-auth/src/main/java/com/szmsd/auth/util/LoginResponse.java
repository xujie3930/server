package com.szmsd.auth.util;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author Mars
 * @Description //TODO 登录返回
 * @Date 2020/6/17
 * @Param
 * @r
 **/
@Data
public class LoginResponse implements Serializable {
    private int code;
    private String msg;
    private Object data;


    public LoginResponse(int code, Object data) {
        this.code = code;
        this.data = data;
    }

}
