package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HttpPickupPackageFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.PickupPackageService;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HtpPickupPackageFeignFallback implements FallbackFactory<HttpPickupPackageFeignService> {
    @Override
    public HttpPickupPackageFeignService create(Throwable throwable) {
        return new HttpPickupPackageFeignService() {

            @Override
            public R<List<PickupPackageService>> services() {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> create(CreatePickupPackageCommand createPickupPackageCommand) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
