package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpSrmFeignService;
import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class HtpSrmFeignFallback implements FallbackFactory<HtpSrmFeignService> {

    @Override
    public HtpSrmFeignService create(Throwable throwable) {
        return new HtpSrmFeignService(){
            @Override
            public R<OperationResultOfIListOfPackageCost> packageCostBatch(PackageCostRequest packageCostRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<OperationResultOfChargeWrapperOfPricingChargeInfo> pricingService(AnalysisInfo analysisInfo) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<OperationResultOfAnalysisRouteResult> routePathRoute(AnalysisInfo analysisInfo) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
