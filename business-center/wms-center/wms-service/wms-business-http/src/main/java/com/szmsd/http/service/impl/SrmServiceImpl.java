package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.service.ISrmService;
import com.szmsd.http.service.http.SrmRequest;
import com.szmsd.http.vo.OperationError;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SrmServiceImpl extends SrmRequest implements ISrmService {

    protected SrmServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public OperationResultOfIListOfPackageCost packageCostBatch(PackageCostRequest packageCostRequest) {
        String text = httpPost(packageCostRequest.getWarehouseCode(), "packageCost.batch", packageCostRequest);
        try {
            return JSON.parseObject(text, OperationResultOfIListOfPackageCost.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            OperationResultOfIListOfPackageCost operationResultOfIListOfPackageCost = new OperationResultOfIListOfPackageCost();
            operationResultOfIListOfPackageCost.setSucceeded(false);
            OperationError operationError = new OperationError();
            operationError.setCode("Request Error");
            operationResultOfIListOfPackageCost.setError(operationError);
            return operationResultOfIListOfPackageCost;
        }
    }

    @Override
    public OperationResultOfChargeWrapperOfPricingChargeInfo pricingService(AnalysisInfo analysisInfo) {
        String text = httpPost(analysisInfo.getWarehouseCode(), "pricing.service", analysisInfo, analysisInfo.getService());
        try {
            return JSON.parseObject(text, OperationResultOfChargeWrapperOfPricingChargeInfo.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            OperationResultOfChargeWrapperOfPricingChargeInfo operationResultOfChargeWrapperOfPricingChargeInfo = new OperationResultOfChargeWrapperOfPricingChargeInfo();
            operationResultOfChargeWrapperOfPricingChargeInfo.setSucceeded(false);
            OperationError operationError = new OperationError();
            operationError.setCode("Request Error");
            operationResultOfChargeWrapperOfPricingChargeInfo.setError(operationError);
            return operationResultOfChargeWrapperOfPricingChargeInfo;
        }
    }

    @Override
    public OperationResultOfAnalysisRouteResult routePathRoute(AnalysisInfo analysisInfo) {
        String text = httpPost(analysisInfo.getWarehouseCode(), "routePath.route", analysisInfo, analysisInfo.getRouteId());
        try {
            return JSON.parseObject(text, OperationResultOfAnalysisRouteResult.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            OperationResultOfAnalysisRouteResult operationResultOfAnalysisRouteResult = new OperationResultOfAnalysisRouteResult();
            operationResultOfAnalysisRouteResult.setSucceeded(false);
            OperationError operationError = new OperationError();
            operationError.setCode("Request Error");
            operationResultOfAnalysisRouteResult.setError(operationError);
            return operationResultOfAnalysisRouteResult;
        }
    }
}
