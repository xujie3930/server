package com.szmsd.chargerules.api.feign.factory;

import com.szmsd.chargerules.api.feign.ChargeFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ChargeFeignFallback implements FallbackFactory<ChargeFeignService> {

    @Override
    public ChargeFeignService create(Throwable throwable) {

        return queryDto -> R.convertResultJson(throwable);

    }
}
