package com.szmsd.exception.api.feign;


import com.szmsd.common.core.domain.R;
import com.szmsd.exception.api.BusinessExceptionInterface;
import com.szmsd.exception.api.factory.ExceptionInfoFeignFallback;
import com.szmsd.exception.dto.ExceptionInfoDto;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.ExceptionInfoFeignFallback", name = BusinessExceptionInterface.SERVICE_NAME, fallbackFactory = ExceptionInfoFeignFallback.class)
public interface ExceptionInfoFeignService {

    @PostMapping(value = "/exception/info/add")
    R newException(@RequestBody NewExceptionRequest newExceptionRequest);

    @PostMapping(value = "/exception/info/process")
    R processException(@RequestBody ProcessExceptionRequest processExceptionRequest);

    @PostMapping(value = "/exception/info/count")
    R<Integer> countException(@RequestBody String sellerCode);

    @PostMapping(value = "/exception/info/ignore")
    R<Integer> ignore(@RequestBody ExceptionInfoDto exceptionInfo);
}
