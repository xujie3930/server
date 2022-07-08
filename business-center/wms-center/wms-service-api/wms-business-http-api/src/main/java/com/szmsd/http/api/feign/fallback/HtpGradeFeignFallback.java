package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.api.feign.HtpGradeFeignService;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Component
public class HtpGradeFeignFallback implements FallbackFactory<HtpGradeFeignService> {
    @Override
    public HtpGradeFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpGradeFeignService() {

            @Override
            public R<PageVO<GradeMainDto>> page(GradePageRequest pageDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R detailResult(String id) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R create(MergeGradeDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R update(MergeGradeDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R detailImport(UpdateGradeDetailDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R customUpdate(UpdateGradeCustomDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
