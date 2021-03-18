package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.factory.SysDictDataServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liulei
 */
@FeignClient(contextId = "sysDictDataFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = SysDictDataServiceFallbackFactory.class)
public interface SysDictDataFeignService {
}
