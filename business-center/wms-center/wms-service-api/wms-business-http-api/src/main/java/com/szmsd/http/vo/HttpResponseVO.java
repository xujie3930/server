package com.szmsd.http.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.utils.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
public class HttpResponseVO implements Serializable {

    /**
     * 响应状态码
     */
    private int status;

    /**
     * 响应头
     */
    private Map<String, String> headers;

    /**
     * 响应内容
     */
    private Object body;

    /**
     * 是否为二进制，true为二进制，false为字符串
     */
    private boolean binary;

    public void checkStatus() {
        String errorMsg = getErrorMsg();
        if (StringUtils.isNotBlank(errorMsg)) {
            throw new RuntimeException("CKRemote【" + errorMsg + "】");
        }
    }

    /**
     * 判断状态
     * @return
     */
    public boolean checkStatusFlag() {
        return StringUtils.isBlank(getErrorMsg());
    }
    /**
     * {
     * "Errors": [
     * {
     * "Code": "request.DeclareName",
     * "Message": "字段 DeclareName 必须与正则表达式“(?![\\d\\s]+$)^[a-zA-Z_\\s0-9\\-\\(\\)\\'&,\\|]+$”匹配。"
     * }
     * ],
     * "TicketId": "54a161ac-dd3f-4206-8bf0-c3926ce1d8d7",
     * "UtcDateTime": "2021-12-15T03:40:43Z",
     * "RequestUri": "/v1/merchantSkus"
     * }
     * 获取错误信息
     *
     * @return ""|| errorMsg
     */
    public String getErrorMsg() {
        if (!(status == HttpStatus.SUCCESS || status == HttpStatus.CREATED)) {
            try {
                ErrorInfo errorInfo = JSONObject.parseObject(body.toString(), ErrorInfo.class);
                return errorInfo.getErrors().stream().map(ErrorMsg::getMessage).collect(Collectors.joining(","));
            } catch (Exception e) {
                return JSONObject.toJSONString(body);
            }
        }
        return "";
    }
}

@NoArgsConstructor
@Data
class ErrorInfo {
    @JsonProperty("Errors")
    private List<ErrorMsg> errors;
    @JsonProperty("TicketId")
    private String ticketId;
    @JsonProperty("UtcDateTime")
    private String utcDateTime;
    @JsonProperty("RequestUri")
    private String requestUri;

}

@NoArgsConstructor
@Data
class ErrorMsg {
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Message")
    private String message;

}