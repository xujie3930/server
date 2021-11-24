package com.szmsd.exception.api.factory;


import com.szmsd.common.core.domain.R;
import com.szmsd.exception.api.feign.ExceptionInfoFeignService;
import com.szmsd.exception.dto.ExceptionInfoDto;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class ExceptionInfoFeignFallback implements FallbackFactory<ExceptionInfoFeignService> {
    @Override
    public ExceptionInfoFeignService create(Throwable throwable) {
        return new ExceptionInfoFeignService() {

            @Override
            public R newException(NewExceptionRequest newExceptionRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R processException(ProcessExceptionRequest processExceptionRequest) {
                return R.convertResultJson(throwable);
            }
            @Override
            public R<Integer> countException(@RequestBody String sellerCode){
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> ignore(ExceptionInfoDto exceptionInfo) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
