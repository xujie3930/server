package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpPricedSheetFeignService;
import com.szmsd.http.dto.CreatePricedSheetCommand;
import com.szmsd.http.dto.PricedSheetCodeCriteria;
import com.szmsd.http.dto.UpdatePricedGradeDto;
import com.szmsd.http.dto.UpdatePricedSheetCommand;
import com.szmsd.http.vo.PricedSheet;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class HtpPricedSheetFeignFallback implements FallbackFactory<HtpPricedSheetFeignService> {
    @Override
    public HtpPricedSheetFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpPricedSheetFeignService() {
            @Override
            public R<PricedSheet> info(String sheetCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> create(CreatePricedSheetCommand createPricedSheetCommand) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> update(UpdatePricedSheetCommand updatePricedSheetCommand) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> updateGrade(UpdatePricedGradeDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> importFile(String sheetCode, MultipartFile file) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<FileStream> exportFile(PricedSheetCodeCriteria pricedSheetCodeCriteria) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
