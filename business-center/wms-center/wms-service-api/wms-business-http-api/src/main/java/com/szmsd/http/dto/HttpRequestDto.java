package com.szmsd.http.dto;

import lombok.Data;
import org.springframework.http.HttpMethod;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@Data
public class HttpRequestDto implements Serializable {

    /**
     * 请求方式
     * 目前支持：GET，POST，PUT，DELETE
     */
    @NotNull(message = "请求方式不能为空")
    private HttpMethod method;

    /**
     * 请求接口路径
     */
    @NotBlank(message = "请求路径不能为空")
    private String uri;

    /**
     * 请求头
     */
    private Map<String, String> headers;

    /**
     * 参数
     * GET：会添加到路径后面
     * POST，PUT，DELETE：会添加到请求body里面（如果是空的会默认设置一个空对象{}放进参数里面）
     */
    private Object body;

}
