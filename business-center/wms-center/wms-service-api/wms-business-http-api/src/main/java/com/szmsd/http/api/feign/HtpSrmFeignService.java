package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpSrmFeignFallback;
import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpRmiFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpSrmFeignFallback.class)
public interface HtpSrmFeignService {

    @PostMapping("/api/srm/http/packageCost/batch")
    R<OperationResultOfIListOfPackageCost> packageCostBatch(@RequestBody PackageCostRequest packageCostRequest);

    @PostMapping("/api/srm/http/pricing/service")
    R<OperationResultOfChargeWrapperOfPricingChargeInfo> pricingService(@RequestBody AnalysisInfo analysisInfo);

    @PostMapping("/api/srm/http/routePath/route")
    R<OperationResultOfAnalysisRouteResult> routePathRoute(@RequestBody AnalysisInfo analysisInfo);
}
