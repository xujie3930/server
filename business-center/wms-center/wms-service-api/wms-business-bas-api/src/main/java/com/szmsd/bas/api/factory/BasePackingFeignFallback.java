package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingConditionQueryDto;
import com.szmsd.bas.dto.BaseProductConditionQueryDto;
import com.szmsd.bas.dto.CreatePackingRequest;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

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

            @Override
            public R<BasePacking> queryByCode(BasePackingConditionQueryDto conditionQueryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R createPackings(CreatePackingRequest createPackingRequest){
                return R.convertResultJson(throwable);
            }
        };
    }
}
