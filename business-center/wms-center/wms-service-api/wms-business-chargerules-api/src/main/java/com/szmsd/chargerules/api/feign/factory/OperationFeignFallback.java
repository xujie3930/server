package com.szmsd.chargerules.api.feign.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OperationFeignFallback implements FallbackFactory<OperationFeignService> {

    @Override
    public OperationFeignService create(Throwable throwable) {
        log.error("调用规则信息失败：", throwable);
        return new OperationFeignService() {
            @Override
            public R delOutboundCharge(DelOutboundOperationVO delOutboundVO) {
                log.error("调用规则信息失败-delOutboundCharge：{}", JSONObject.toJSONString(delOutboundVO));
                return R.convertResultJson(throwable);
            }

            @Override
            public R delOutboundThaw(DelOutboundOperationVO delOutboundVO) {
                log.error("调用规则信息失败-delOutboundThaw：{}", JSONObject.toJSONString(delOutboundVO));
                return R.convertResultJson(throwable);
            }

            @Override
            public R delOutboundFreeze(DelOutboundOperationVO delOutboundVO) {
                log.error("调用规则信息失败-delOutboundFreeze：{}", JSONObject.toJSONString(delOutboundVO));
                return R.convertResultJson(throwable);
            }
        };
    }
}
