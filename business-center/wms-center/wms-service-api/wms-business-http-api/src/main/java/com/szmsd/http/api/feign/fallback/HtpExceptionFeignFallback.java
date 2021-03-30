package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.api.feign.HtpExceptionFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Component
public class HtpExceptionFeignFallback implements FallbackFactory<HtpExceptionFeignService> {
    @Override
    public HtpExceptionFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpExceptionFeignService() {
            @Override
            public R<ResponseVO> processing(@RequestBody ExceptionProcessRequest exceptionProcessRequest){
                return R.convertResultJson(throwable);
            }
        };
    }
}
