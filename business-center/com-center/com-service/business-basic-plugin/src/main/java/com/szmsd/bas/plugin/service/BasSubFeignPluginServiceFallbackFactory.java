package com.szmsd.bas.plugin.service;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:45
 */
@Component
public class BasSubFeignPluginServiceFallbackFactory implements FallbackFactory<BasSubFeignPluginService> {

    @Override
    public BasSubFeignPluginService create(Throwable throwable) {
        return new BasSubFeignPluginService() {
            @Override
            public R<Map<String, List<BasSubWrapperVO>>> getSub(String code) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
