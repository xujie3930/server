package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BasePackingFeignFallback implements FallbackFactory<BasePackingFeignService> {

    @Override
    public BasePackingFeignService create(Throwable throwable) {
        return new BasePackingFeignService() {

            @Override
            public R<List<BasePacking>> queryPackingList(BaseProductConditionQueryDto conditionQueryDto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
