package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpShipmentOrderFeignService;
import com.szmsd.http.api.feign.HtpWarMappingFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: HtpWarMappingFeignFallback
 * @Description:
 * @Author: 11
 * @Date: 2021-12-14 15:12
 */
@Slf4j
public class HtpWarMappingFeignFallback implements FallbackFactory<HtpWarMappingFeignService> {
    @Override
    public HtpWarMappingFeignService create(Throwable throwable) {
        log.error("调用仓库映射异常：", throwable);
        return new HtpWarMappingFeignService() {
            @Override
            public R<String> getMappingWarCode(String warehouseCode) {
                log.error("获取仓库映射异常：{}", warehouseCode);
                return R.failed("获取仓库映射异常:" + throwable.getMessage());
            }
        };
    }
}
