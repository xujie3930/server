package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.feign.BasRegionFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:26
 */
@Component
public class BasRegionFeignServiceFallbackFactory implements FallbackFactory<BasRegionFeignService> {

    @Override
    public BasRegionFeignService create(Throwable throwable) {
        return new BasRegionFeignService() {
            @Override
            public R<BasRegionSelectListVO> queryByCountryCode(String addressCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
