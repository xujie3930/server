package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.dto.AnalysisInfo;
import com.szmsd.http.dto.PackageCostRequest;
import com.szmsd.http.service.ISrmService;
import com.szmsd.http.vo.OperationResultOfAnalysisRouteResult;
import com.szmsd.http.vo.OperationResultOfChargeWrapperOfPricingChargeInfo;
import com.szmsd.http.vo.OperationResultOfIListOfPackageCost;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 */
@Api(tags = {"SRM管理"})
@ApiSort(200)
@RestController
@RequestMapping("/api/srm/http")
public class SrmController extends BaseController {

    @Autowired
    private ISrmService srmService;

    @PostMapping("/packageCost/batch")
    @ApiOperation(value = "SRM管理 - HTTP - #Q100 批量获取包裹成本信息，其中处理号，最多支持1000个，多个请分开调用", position = 100)
    @ApiImplicitParam(name = "packageCostRequest", value = "PackageCostRequest", dataType = "PackageCostRequest")
    public R<OperationResultOfIListOfPackageCost> packageCostBatch(@RequestBody PackageCostRequest packageCostRequest) {
        return R.ok(srmService.packageCostBatch(packageCostRequest));
    }

    @PostMapping("/pricing/service")
    @ApiOperation(value = "SRM管理 - HTTP - #Q200 根据供应商服务，计算包裹成本", position = 200)
    @ApiImplicitParam(name = "analysisInfo", value = "AnalysisInfo", dataType = "AnalysisInfo")
    public R<OperationResultOfChargeWrapperOfPricingChargeInfo> pricingService(@RequestBody AnalysisInfo analysisInfo) {
        return R.ok(srmService.pricingService(analysisInfo));
    }

    @PostMapping("/routePath/route")
    @ApiOperation(value = "SRM管理 - HTTP - #Q300 根据线路图编号，选择线路图", position = 300)
    @ApiImplicitParam(name = "analysisInfo", value = "AnalysisInfo", dataType = "AnalysisInfo")
    public R<OperationResultOfAnalysisRouteResult> routePathRoute(@RequestBody AnalysisInfo analysisInfo) {
        return R.ok(srmService.routePathRoute(analysisInfo));
    }

}
