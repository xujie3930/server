package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import com.szmsd.http.dto.CreatePricedProductCommand;
import com.szmsd.http.dto.GetPricedProductsCommand;
import com.szmsd.http.dto.PricedProductSearchCriteria;
import com.szmsd.http.vo.DirectServiceFeeData;
import com.szmsd.http.vo.KeyValuePair;
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
        };
    }
}
