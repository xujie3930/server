package com.szmsd.finance.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.RechargesFeignService;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author liulei
 */
@Component
@Slf4j
public class RechargeFeignFallback implements FallbackFactory<RechargesFeignService> {
    @Override
    public RechargesFeignService create(Throwable cause) {
        return new RechargesFeignService(){

            @Override
            public R rechargeCallback(RechargesCallbackRequestDTO requestDTO) {
                log.info("充值回调失败，服务调用降级");
                return null;
            }

            @Override
            public R warehouseFeeDeductions(CustPayDTO dto) {
                log.info("仓储费扣款失败，服务调用降级");
                return null;
            }

        };
    }
}
