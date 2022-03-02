package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpSrmFeignService;
import com.szmsd.http.api.service.IHtpSrmClientService;
import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HtpSrmClientServiceImpl implements IHtpSrmClientService {

    @Autowired
    private HtpSrmFeignService htpSrmFeignService;

    @Override
    public OperationResultOfIListOfPackageCost packageCostBatch(PackageCostRequest packageCostRequest) {
        return R.getDataAndException(this.htpSrmFeignService.packageCostBatch(packageCostRequest));
    }

    @Override
    public OperationResultOfChargeWrapperOfPricingChargeInfo pricingService(AnalysisInfo analysisInfo) {
        return R.getDataAndException(this.htpSrmFeignService.pricingService(analysisInfo));
    }

    @Override
    public OperationResultOfAnalysisRouteResult routePathRoute(AnalysisInfo analysisInfo) {
        return R.getDataAndException(this.htpSrmFeignService.routePathRoute(analysisInfo));
    }
}
