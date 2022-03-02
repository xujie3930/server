package com.szmsd.http.api.service;

import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;

public interface IHtpSrmClientService {

    OperationResultOfIListOfPackageCost packageCostBatch(PackageCostRequest packageCostRequest);

    OperationResultOfChargeWrapperOfPricingChargeInfo pricingService(AnalysisInfo analysisInfo);

    OperationResultOfAnalysisRouteResult routePathRoute(AnalysisInfo analysisInfo);
}
