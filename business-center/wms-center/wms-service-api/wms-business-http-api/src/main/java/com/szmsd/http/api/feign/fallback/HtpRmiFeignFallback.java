package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpRmiFeignFallback implements FallbackFactory<HtpRmiFeignService> {
    @Override
    public HtpRmiFeignService create(Throwable throwable) {
        log.error("【RMI】调用异常：", throwable);
        return new HtpRmiFeignService() {
            @Override
            public R<HttpResponseVO> rmi(HttpRequestDto dto) {
                log.error("【RMI】rmi 调用异常：{}", dto);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<HttpResponseVO> rmiSync(HttpRequestDto dto) {
                log.error("【RMI】rmiSync 调用异常：{}", dto);
                return R.convertResultJson(throwable);
            }
        };
    }
}
