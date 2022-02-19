package com.szmsd.pack.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.pack.api.BusinessPackageInterface;
import com.szmsd.pack.api.feign.factory.PackageCollectionFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.PackageCollectionFeignService", name = BusinessPackageInterface.SERVICE_NAME, fallbackFactory = PackageCollectionFeignFallback.class)
public interface PackageCollectionFeignService {

    /**
     * 交货管理 - 揽收 - 修改状态为揽收中
     *
     * @param collectionNo collectionNo
     * @return Integer
     */
    @PostMapping("/package-collection/updateCollecting")
    R<Integer> updateCollecting(@RequestBody String collectionNo);
}
