package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpChaLevelMaintenanceFeignService;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.http.vo.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HtpChaLevelMaintenanceFeignFallback implements FallbackFactory<HtpChaLevelMaintenanceFeignService> {
    @Override
    public HtpChaLevelMaintenanceFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpChaLevelMaintenanceFeignService() {

            @Override
            public R<PageVO<ChaLevelMaintenanceDto>> page(ChaLevelMaintenancePageRequest pageDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<ChaLevelMaintenanceDto>> allList(ChaLevelMaintenanceDto pageDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R create(ChaLevelMaintenanceDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R update(ChaLevelMaintenanceDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R delete(String id) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R get(String id) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
