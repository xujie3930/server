package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Component
public class HtpBasFeignFallback implements FallbackFactory<HtpBasFeignService> {
    @Override
    public HtpBasFeignService create(Throwable throwable) {
        log.info("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpBasFeignService() {
            @Override
            public R<ResponseVO> createPacking(@RequestBody PackingRequest packingRequest)
            {
                return R.convertResultJson(throwable);
            }
            @Override
            public R<ResponseVO> createProduct(@RequestBody ProductRequest productRequest){
                return R.convertResultJson(throwable);
            }
            @Override
            public R<ResponseVO> createSeller(@RequestBody SellerRequest sellerRequest){
                return R.convertResultJson(throwable);
            }
            @Override
            public R<ResponseVO> specialOperationType(SpecialOperationRequest specialOperationRequest) {
                return R.convertResultJson(throwable);
            }

        };
    }
}
