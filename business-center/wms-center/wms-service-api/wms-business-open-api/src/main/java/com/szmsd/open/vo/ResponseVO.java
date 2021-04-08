package com.szmsd.open.vo;

import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
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
    private boolean success;

    @ApiModelProperty(value = "返回消息")
    private String message;

    public ResponseVO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseVO() {
    }

    public static ResponseVO ok() {
        return new ResponseVO(true, null);
    }

    public static ResponseVO ok(String message) {
        return new ResponseVO(true, message);
    }

    public static ResponseVO failed(String message) {
        return new ResponseVO(false, message == null ? null : message.replaceAll("(?s)(运行时异常:)(?=.*\\1)", ""));
    }

    public static <T> ResponseVO unknown(R<T> r) {
        boolean flag = r.getCode() == HttpStatus.SUCCESS;
        return flag ? ok() : failed(r.getMsg());
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
