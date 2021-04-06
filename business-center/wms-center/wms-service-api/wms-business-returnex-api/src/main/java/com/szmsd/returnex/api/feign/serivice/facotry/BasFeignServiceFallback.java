package com.szmsd.returnex.api.feign.serivice.facotry;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.returnex.api.feign.serivice.IBasFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: BasFeignServiceFallback
 * @Description: bas
 * @Author: 11
 * @Date: 2021/4/6 14:36
 */
@Slf4j
@Component
public class BasFeignServiceFallback implements FallbackFactory<IBasFeignService> {
    @Override
    public IBasFeignService create(Throwable throwable) {
        return new IBasFeignService() {

            @Override
            public R<String> getLoginSellerCode() {
                throw new BaseException("获取用户sellerCode异常!");
            }
        };
    }
}
