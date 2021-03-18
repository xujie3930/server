package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HttpRechargeFeignService;
import com.szmsd.http.dto.recharges.RechargesRequestDTO;
import com.szmsd.http.vo.RechargesResponseVo;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liulei
 */
@Slf4j
@Component
public class HttpRechargeFeignServiceFallback implements FallbackFactory<HttpRechargeFeignService> {
    @Override
    public HttpRechargeFeignService create(Throwable throwable) {
        return new HttpRechargeFeignService(){
            @Override
            public R<RechargesResponseVo> onlineRecharge(RechargesRequestDTO dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
