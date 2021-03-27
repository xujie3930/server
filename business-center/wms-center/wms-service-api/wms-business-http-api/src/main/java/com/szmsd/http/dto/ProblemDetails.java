package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 14:23
 */
@Data
@Accessors(chain = true)
public class ProblemDetails implements Serializable {

    private String type;
    private String title;
    private Integer status;
    private String detail;
    private String instance;
    // 错误集合2
    private List<ErrorDto2> Errors;
}
