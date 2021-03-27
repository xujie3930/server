package com.szmsd.http.api.feign;

import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HttpReturnExpressServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName: HttpReturnExpressService
 * @Description: 退件 http调用API
 * @Author: 11
 * @Date: 2021/3/26 14:03
 */
@FeignClient(contextId = "FeignClient.HttpReturnExpressFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HttpReturnExpressServiceFallback.class)
public interface HttpReturnExpressFeignService {

}
