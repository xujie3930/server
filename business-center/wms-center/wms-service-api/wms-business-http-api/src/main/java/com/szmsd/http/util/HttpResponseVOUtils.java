package com.szmsd.http.util;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.discount.DiscountMainDto;
import com.szmsd.http.vo.DateOperation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class HttpResponseVOUtils {

    public static void main(String[] args) {
        System.out.println(JSON.parseObject("{\"time\":\"9999-12-31 23:59:59\"}", DateOperation.class).getTime().getTime());


    }
    @NoArgsConstructor
    @Data
    static class ErrorInfo {
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
    static class ErrorMsg {
        @JsonProperty("Code")
        private String code;
        @JsonProperty("Message")
        private String message;

    }

    public static String getErrorMsg(HttpResponseBody hrb) {
        if (!(hrb.getStatus() == com.szmsd.common.core.constant.HttpStatus.SUCCESS || hrb.getStatus() == com.szmsd.common.core.constant.HttpStatus.CREATED)) {
            try {
                ErrorInfo errorInfo = JSONObject.parseObject(hrb.getBody(), ErrorInfo.class);
                if(errorInfo.getErrors() == null || errorInfo.getErrors().size() == 0
                        || StringUtils.isEmpty(errorInfo.getErrors().get(0).getMessage())){
                    return hrb.getBody();
                }
                return errorInfo.getErrors().stream().map(ErrorMsg::getMessage).collect(Collectors.joining(","));
            } catch (Exception e) {
                return JSONObject.toJSONString(hrb.getBody());
            }
        }
        return "";
    }

    public static R transformation(HttpResponseBody hrb){
        return transformation(hrb, null);
    }
    public static R transformation(HttpResponseBody hrb, Class cls){
        return transformation(hrb, cls, null);
    }

    public static R transformation(HttpResponseBody hrb, Class cls, Class failCls){
        if (HttpStatus.SC_OK == hrb.getStatus()) {
            if(cls == null){
                return R.ok();
            }else{
                String newBody = newBody(hrb.getBody());
                return R.ok(JSON.parseObject(newBody, cls));
            }
        } else {
            return R.failed(getErrorMsg(hrb));
        }

    }

    public static String newBody(String json){
        if(StringUtils.isNotEmpty(json)){
            return json.replaceAll("9999-12-31T23:59:59.9999999Z","9999-12-31 23:59:59");
        }
        return json;
    }


    public static R transformationList(HttpResponseBody hrb, Class cls){
        if (HttpStatus.SC_OK == hrb.getStatus()) {
            String newBody = newBody(hrb.getBody());
            return R.ok(JSON.parseArray(newBody, cls));
        } else {
            return R.failed(getErrorMsg(hrb));
        }

    }
}

