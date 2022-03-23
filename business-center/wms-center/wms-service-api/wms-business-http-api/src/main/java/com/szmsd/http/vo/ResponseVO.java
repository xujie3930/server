package com.szmsd.http.vo;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.http.annotation.ErrorSerializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

    public static void statusAssert(R<? extends ResponseVO> result, String api) {
        log.info("{}[{}]", api, JSON.toJSONString(result));
        AssertUtil.notNull(result, () -> "RemoteRequest[" + api + "请求失败]");

        boolean expression = result.getCode() == HttpStatus.SUCCESS;
        AssertUtil.isTrue(expression, () -> "RemoteRequest[" + api + "失败:" + result.getMsg() + "]");

    }

    public static void resultAssert(R<? extends ResponseVO> result, String api) {
        statusAssert(result, api);
        ResponseVO data = result.getData();
        boolean expression1 = data == null || (data.getSuccess() == null ? false : data.getSuccess());
        AssertUtil.isTrue(expression1, () -> "RemoteRequest[" + api + "失败:" + getDefaultStr(data.getMessage()).concat(getDefaultStr(data.getErrors())) + "]");
    }

    public static String getDefaultStr(String str) {
        return Optional.ofNullable(str).orElse("");
    }

    public static ResponseVO build(String text) {
        try {
            return JSON.parseObject(text, ResponseVO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ResponseVO responseVO = new ResponseVO();
            responseVO.setSuccess(false);
            responseVO.setMessage(text);
            return responseVO;
        }
    }

    public static void resultAssert(ResponseVO responseVO, String api) {
        if (null == responseVO) return;
        String code = Optional.ofNullable(responseVO.getCode()).orElse("500");

        if (!("" + org.apache.http.HttpStatus.SC_OK).equals(code) || !responseVO.getSuccess()) {
            // 不是正常返回
            String errors = Optional.ofNullable(responseVO.getErrors()).orElse("调用异常!");
            String mssage = Optional.ofNullable(responseVO.getMessage()).orElse("调用异常!");
            AssertUtil.isTrue(false, () -> "RemoteRequest[" + api + "失败:" + getDefaultStr(errors).concat(mssage) + "]");
        }
    }
}
