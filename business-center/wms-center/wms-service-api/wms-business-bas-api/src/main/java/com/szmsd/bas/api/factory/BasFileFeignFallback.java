package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasFileFeignService;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;

public class BasFileFeignFallback   implements FallbackFactory<BasFileFeignService> {
    @Override
    public BasFileFeignService create(Throwable throwable) {
        return new BasFileFeignService() {
            @Override
            public R addbasFile(BasFile basFile) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updatebasFile(BasFile basFile) {
                  return R.convertResultJson(throwable);
            }
        };
    }
}
