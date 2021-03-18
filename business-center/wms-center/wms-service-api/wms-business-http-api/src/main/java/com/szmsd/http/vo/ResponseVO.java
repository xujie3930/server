package com.szmsd.http.vo;

import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.http.annotation.ErrorSerializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 13:55
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ResponseVO", description = "ResponseVO对象")
public class ResponseVO implements Serializable {

    @ApiModelProperty(value = "是否执行成功")
    private Boolean success;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "错误编码")
    private String code;

    @ApiModelProperty(value = "错误信息")
    @ErrorSerializable
    private String errors;

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
}
