package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
import com.szmsd.http.vo.PricedProductInfo;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HtpPricedProductFeignFallback implements FallbackFactory<HtpPricedProductFeignService> {
    @Override
    public HtpPricedProductFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return new HtpPricedProductFeignService() {
            @Override
            public R<List<DirectServiceFeeData>> pricedProducts(GetPricedProductsCommand getPricedProductsCommand) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<KeyValuePair>> keyValuePairs() {
                return R.convertResultJson(throwable);
            }

            @Override
            public PageVO pageResult(PricedProductSearchCriteria pricedProductSearchCriteria) {
                return PageVO.empty();
            }

            @Override
            public R<ResponseVO> create(CreatePricedProductCommand createPricedProductCommand) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<PricedProductInfo> info(String productCode) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> update(UpdatePricedProductCommand updatePricedProductCommand) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<FileStream> exportFile(PricedProductCodesCriteria pricedProductCodesCriteria) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
