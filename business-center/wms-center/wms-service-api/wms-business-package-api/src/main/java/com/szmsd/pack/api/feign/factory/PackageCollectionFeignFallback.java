package com.szmsd.pack.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.pack.api.feign.PackageCollectionFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PackageCollectionFeignFallback implements FallbackFactory<PackageCollectionFeignService> {

    @Override
    public PackageCollectionFeignService create(Throwable throwable) {
        return new PackageCollectionFeignService() {
            @Override
            public R<Integer> updateCollecting(String collectionNo) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
