package com.szmsd.http.vo;

import com.szmsd.common.core.exception.com.CommonException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

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

    public static void assertResponse(ResponseVO responseVO, String message) {
        if (Objects.isNull(responseVO)) {
            throw new CommonException("999", message);
        }
        if (!responseVO.getSuccess()) {
            if (StringUtils.isNotEmpty(responseVO.getMessage())) {
                throw new CommonException("999", responseVO.getMessage());
            }
            throw new CommonException("999", message);
        }
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
