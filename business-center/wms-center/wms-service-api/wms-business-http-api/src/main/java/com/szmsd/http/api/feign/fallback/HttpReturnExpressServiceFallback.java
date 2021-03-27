package com.szmsd.http.api.feign.fallback;

import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HttpReturnExpressFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: HttpReturnExpressServiceFallback
 * @Description: 退件服务调用异常处理
 * @Author: 11
 * @Date: 2021/3/26 14:05
 */
@Slf4j
@Component
public class HttpReturnExpressServiceFallback implements FallbackFactory<HttpReturnExpressFeignService> {
    @Override
    public HttpReturnExpressFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HttpReturnExpressFeignService() {

        };
    }
}
