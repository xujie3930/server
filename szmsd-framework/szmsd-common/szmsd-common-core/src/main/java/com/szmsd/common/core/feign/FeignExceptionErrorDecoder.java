package com.szmsd.common.core.feign;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class FeignExceptionErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        try {
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader(StandardCharsets.UTF_8));
                R<?> r = JSON.parseObject(body, R.class);
                if (null != r) {
                    throw new CommonException(String.valueOf(r.getCode()), r.getMsg());
                }
            }
        } catch (Exception e) {
            if (e instanceof CommonException) {
                return e;
            }
            return new CommonException("998", e.getMessage());
        }
        return new CommonException("998", "系统异常,请联系管理员");
    }
}
