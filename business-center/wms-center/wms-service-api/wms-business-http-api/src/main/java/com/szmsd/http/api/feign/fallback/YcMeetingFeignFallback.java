package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;

import com.szmsd.http.api.feign.YcMeetingFeignService;
import com.szmsd.http.domain.YcAppParameter;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class YcMeetingFeignFallback implements FallbackFactory<YcMeetingFeignService> {

    @Override
    public YcMeetingFeignService create(Throwable throwable) {
        return new YcMeetingFeignService(){
            @Override
            public R<List<YcAppParameter>> selectBasYcappConfig() {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Map> YcApiri(@RequestBody YcAppParameter ycAppParameter) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
