package com.szmsd.open.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 13:55
 */
@ApiModel(value = "ResponseVO", description = "ResponseVO对象")
public class ResponseVO implements Serializable {

    @ApiModelProperty(value = "是否执行成功")
    private Boolean success;

    @ApiModelProperty(value = "返回消息")
    private String message;

    public ResponseVO(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseVO() {
    }

    public static ResponseVO ok() {
        return new ResponseVO(true, null);
    }

    public static ResponseVO failed(String message) {
        return new ResponseVO(false, message);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
