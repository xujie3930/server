package com.szmsd.http.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-01 15:29
 */
@Data
public class BaseOperationResponse implements Serializable {

    private Boolean success;

    private String message;
}
