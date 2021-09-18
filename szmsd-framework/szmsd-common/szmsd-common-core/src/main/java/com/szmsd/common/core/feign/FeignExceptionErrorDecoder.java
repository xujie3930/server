package com.szmsd.common.core.feign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.com.SystemException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FeignExceptionErrorDecoder implements ErrorDecoder {
    private final Logger logger = LoggerFactory.getLogger(FeignExceptionErrorDecoder.class);

    @Override
    public Exception decode(String s, Response response) {
        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                log.info("feign decode body {}", JSONObject.toJSONString(body));
                R<?> r = JSON.parseObject(body, R.class);
                if (null != r) {
                    if (HttpStatus.BAD_REQUEST.value() == r.getCode()) {
                        throw new CommonException(String.valueOf(r.getCode()), r.getMsg());
                    } else {
                        throw new SystemException(String.valueOf(r.getCode()), r.getMsg());
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (e instanceof CommonException) {
                return e;
            }
            return new SystemException("500", e.getMessage());
        }
        return new SystemException("500", "系统异常,请联系管理员");
    }
}
