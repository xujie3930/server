package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpDiscountFeignService;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.discount.*;
import com.szmsd.http.dto.grade.GradeMainDto;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpDiscountFeignFallback implements FallbackFactory<HtpDiscountFeignService> {
    @Override
    public HtpDiscountFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpDiscountFeignService() {

            @Override
            public R<OperationRecordDto> operationRecord(String id) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<PageVO<DiscountMainDto>> page(DiscountPageRequest pageDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R detailResult(String id) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R create(MergeDiscountDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R update(MergeDiscountDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R detailImport(UpdateDiscountDetailDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R customUpdate(UpdateDiscountCustomDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
