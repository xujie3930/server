package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSkuRuleMatchingFeignService;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.szmsd.bas.dto.*;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BasSkuRuleMatchingFeignFallback implements FallbackFactory<BasSkuRuleMatchingFeignService> {

    @Override
    public BasSkuRuleMatchingFeignService create(Throwable throwable) {
        return new BasSkuRuleMatchingFeignService() {


            @Override
            public R<List<BasSkuRuleMatching>> getList(BasSkuRuleMatchingDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasOtherRules> getInfo(String sellerCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<BasDeliveryServiceMatching>> getList(BasDeliveryServiceMatchingDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
