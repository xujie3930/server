package com.szmsd.delivery.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelOutboundDto;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:33
 */
@Component
public class DelOutboundFeignFallback implements FallbackFactory<DelOutboundFeignService> {
    @Override
    public DelOutboundFeignService create(Throwable throwable) {
        return new DelOutboundFeignService() {
            @Override
            public R<Integer> add(DelOutboundDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
