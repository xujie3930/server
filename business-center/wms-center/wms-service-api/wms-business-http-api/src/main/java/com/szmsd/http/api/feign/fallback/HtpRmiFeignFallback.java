package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpRmiFeignService;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.vo.HttpResponseVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HtpRmiFeignFallback implements FallbackFactory<HtpRmiFeignService> {
    @Override
    public HtpRmiFeignService create(Throwable throwable) {
        return new HtpRmiFeignService() {
            @Override
            public R<HttpResponseVO> rmi(HttpRequestDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
