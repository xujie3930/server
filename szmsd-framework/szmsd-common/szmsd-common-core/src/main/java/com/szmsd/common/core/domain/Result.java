package com.szmsd.common.core.domain;

import com.szmsd.common.core.constant.CommonConstant;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @FileName Response.java
 * @Description 消息返回对象
 * @Date 2020-06-24 14:52
 * @Author Yan Hua
 * @Version 1.0
 */
@Getter
@Setter
public class Result<T> implements Serializable {

    /**
     * 消息代码<br>
     */
    private String code;
    /**
     * 消息内容<br>
     */
    private String message;
    /**
     * 业务数据对象<br>
     */
    private T data;

    /**
     * 构造器<br>
     */
    public Result() {
    }

    /**
     * ResponseDto
     *
     * @param code
     * @param message
     * @param data
     */
    protected Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 静态创建方法<br>
     *
     * @param data T
     * @return ResponseDto
     */
    public static <T> Result<T> getSuccessResponseDto(T data) {
        return new Result<>(CommonConstant.SUCCESS_CODE, CommonConstant.SUCCESS_MSG, data);
    }

}
