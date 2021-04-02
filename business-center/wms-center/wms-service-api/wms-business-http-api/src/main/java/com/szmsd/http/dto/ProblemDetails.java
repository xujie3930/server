package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * 获取message or null
     *
     * @param problemDetails problemDetails
     * @return String
     */
    public static String getErrorMessageOrNull(ProblemDetails problemDetails) {
        if (null != problemDetails) {
            return problemDetails.getErrorMessage();
        }
        return null;
    }

    /**
     * 获取message
     *
     * @return String
     */
    public String getErrorMessage() {
        String message = null;
        List<ErrorDto2> errors = this.getErrors();
        if (CollectionUtils.isNotEmpty(errors)) {
            ErrorDto2 errorDto2 = errors.get(0);
            if (StringUtils.isNotEmpty(errorDto2.getCode())) {
                message = "[" + errorDto2.getCode() + "]" + errorDto2.getMessage();
            } else {
                message = errorDto2.getMessage();
            }
        }
        return message;
    }
}
