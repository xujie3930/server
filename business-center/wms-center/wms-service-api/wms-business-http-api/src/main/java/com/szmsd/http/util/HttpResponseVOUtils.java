package com.szmsd.http.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.PageVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.stream.Collectors;

public class HttpResponseVOUtils {

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

    private static String getErrorMsg(HttpResponseBody hrb) {
        if (!(hrb.getStatus() == com.szmsd.common.core.constant.HttpStatus.SUCCESS || hrb.getStatus() == com.szmsd.common.core.constant.HttpStatus.CREATED)) {
            try {
                ErrorInfo errorInfo = JSONObject.parseObject(hrb.getBody(), ErrorInfo.class);
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
                return R.ok(JSON.parseObject(hrb.getBody(), cls));
            }
        } else {
            return R.failed(getErrorMsg(hrb));
        }

    }
    public static R<PageVO> transformationPage(HttpResponseBody hrb, Class cls){
        if (HttpStatus.SC_OK == hrb.getStatus()) {
            PageVO pageVO = JSON.parseObject(hrb.getBody(), PageVO.class);
            pageVO.setData(BeanMapperUtil.mapList(pageVO.getData(), cls));
            return R.ok(pageVO);
        } else {
            return R.failed(getErrorMsg(hrb));
        }

    }
}

