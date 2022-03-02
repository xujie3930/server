package com.szmsd.http.service;

import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;

public interface ISrmService {

    /**
     * 批量获取包裹成本信息，其中处理号，最多支持1000个，多个请分开调用
     *
     * @param packageCostRequest packageCostRequest
     * @return OperationResultOfIListOfPackageCost
     */
    OperationResultOfIListOfPackageCost packageCostBatch(PackageCostRequest packageCostRequest);

    /**
     * 根据供应商服务，计算包裹成本
     *
     * @param analysisInfo analysisInfo
     * @return OperationResultOfChargeWrapperOfPricingChargeInfo
     */
    OperationResultOfChargeWrapperOfPricingChargeInfo pricingService(AnalysisInfo analysisInfo);

    /**
     * 根据线路图编号，选择线路图
     *
     * @param analysisInfo analysisInfo
     * @return OperationResultOfAnalysisRouteResult
     */
    OperationResultOfAnalysisRouteResult routePathRoute(AnalysisInfo analysisInfo);
}
