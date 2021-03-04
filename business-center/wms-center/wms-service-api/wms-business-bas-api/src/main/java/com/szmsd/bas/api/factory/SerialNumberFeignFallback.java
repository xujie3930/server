package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.SerialNumberFeignService;
import com.szmsd.bas.dto.GenerateNumberDto;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2020-11-10 010 10:01
 */
@Component
public class SerialNumberFeignFallback implements FallbackFactory<SerialNumberFeignService> {

    @Override
    public SerialNumberFeignService create(Throwable throwable) {
        return new SerialNumberFeignService() {
            @Override
            public R<String> generateNumber(GenerateNumberDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<String>> generateNumbers(GenerateNumberDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
