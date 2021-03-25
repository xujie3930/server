package com.szmsd.http.vo;

import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.http.annotation.ErrorSerializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

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

    public static void resultAssert(R<? extends ResponseVO> result, String api) {
        AssertUtil.notNull(result, () -> "RemoteRequest[" + api + "请求失败]");

        boolean expression = result.getCode() == HttpStatus.SUCCESS;
        AssertUtil.isTrue(expression, () -> "RemoteRequest[" + api + "失败:" +  result.getMsg() + "]");

        ResponseVO data = result.getData();
        boolean expression1 = data == null || (data != null && data.getSuccess() == new Boolean(true));
        AssertUtil.isTrue(expression1, () -> "RemoteRequest[" + api + "失败:" +  getDefaultStr(data.getMessage()).concat(getDefaultStr(data.getErrors())) + "]");
    }

    public static String getDefaultStr(String str) {
        return Optional.ofNullable(str).orElse("");
    }
}
