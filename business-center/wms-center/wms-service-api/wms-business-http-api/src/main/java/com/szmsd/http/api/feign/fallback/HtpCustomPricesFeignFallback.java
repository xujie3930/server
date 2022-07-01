package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.DiscountMainDto;
import com.szmsd.http.dto.grade.GradeMainDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpCustomPricesFeignFallback implements FallbackFactory<HtpCustomPricesFeignService> {
    @Override
    public HtpCustomPricesFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpCustomPricesFeignService() {

            @Override
            public R<CustomPricesMainDto> result(String clientCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updateDiscount(UpdateCustomDiscountMainDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updateGrade(UpdateCustomGradeMainDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updateGradeDetail(CustomGradeMainDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R updateDiscountDetail(CustomDiscountMainDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<GradeMainDto> gradeDetailResult(String id) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<DiscountMainDto> discountDetailResult(String id) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
