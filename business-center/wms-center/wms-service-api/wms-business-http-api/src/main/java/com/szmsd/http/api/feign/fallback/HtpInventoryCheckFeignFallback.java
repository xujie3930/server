package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpInventoryCheckFeignService;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpInventoryCheckFeignFallback implements FallbackFactory<HtpInventoryCheckFeignService> {

    @Override
    public HtpInventoryCheckFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpInventoryCheckFeignService() {

            @Override
            public R<ResponseVO> counting(CountingRequest countingRequest) {
                return R.convertResultJson(throwable);

            }

        };
    }

}
