package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpInboundFeignService;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpInboundFeignFallback implements FallbackFactory<HtpInboundFeignService> {
    @Override
    public HtpInboundFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpInboundFeignService() {
            @Override
            public R<CreateReceiptResponse> create(CreateReceiptRequest createReceiptRequestDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> cancel(CancelReceiptRequest cancelReceiptRequestDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
