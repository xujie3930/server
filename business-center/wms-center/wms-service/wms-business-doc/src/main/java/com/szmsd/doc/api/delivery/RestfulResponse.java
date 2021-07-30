package com.szmsd.doc.api.delivery;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RestfulResponse<T> {

    /**
     * 错误类型
     */
    @ApiModelProperty(value = "错误类型")
    private String error;

    @ApiModelProperty(value = "是否执行成功")
    private boolean success;
    /**
     * 信息
     */
    @ApiModelProperty(value = "错误信息")
    private String message;

    /**
     * 数据
     */
    @ApiModelProperty(value = "数据")
    private T data;

    public RestfulResponse() {
        this.success = true;
    }

    public RestfulResponse(String error, boolean success, String message, T data) {
        this.error = error;
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> RestfulResponse<T> ok() {
        return new RestfulResponse<>();
    }

    public static <T> RestfulResponse<T> ok(T data) {
        return new RestfulResponse<>(null, true, null, data);
    }

}
