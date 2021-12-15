package com.szmsd.http.vo;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.exception.com.AssertUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

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
        if (!(status == HttpStatus.SUCCESS || status == HttpStatus.CREATED)) {
            String errorMsg = "";
            try {
                /**
                 * {
                 *     "Errors": [
                 *         {
                 *             "Code": "request.DeclareName",
                 *             "Message": "字段 DeclareName 必须与正则表达式“(?![\\d\\s]+$)^[a-zA-Z_\\s0-9\\-\\(\\)\\'&,\\|]+$”匹配。"
                 *         }
                 *     ],
                 *     "TicketId": "54a161ac-dd3f-4206-8bf0-c3926ce1d8d7",
                 *     "UtcDateTime": "2021-12-15T03:40:43Z",
                 *     "RequestUri": "/v1/merchantSkus"
                 * }
                 */
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(body));
                Object errors = jsonObject.get("Errors");
                if (null != errors) {
                    errorMsg = JSONObject.toJSONString(errors);
                    throw new RuntimeException("CKRemote【" + errorMsg + "】");
                }
            } catch (Exception e) {
                throw new RuntimeException("CKRemote【" + JSONObject.toJSONString(body) + "】");
            }
        }

    }
}
