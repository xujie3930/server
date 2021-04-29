package com.szmsd.delivery.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundReportFeignService;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-29 11:29
 */
@Component
public class DelOutboundReportFeignFallback implements FallbackFactory<DelOutboundReportFeignService> {

    @Override
    public DelOutboundReportFeignService create(Throwable throwable) {
        return new DelOutboundReportFeignService() {
            @Override
            public R<List<DelOutboundReportListVO>> queryBringVerifyData(DelOutboundReportQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<DelOutboundReportListVO>> queryOutboundData(DelOutboundReportQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
