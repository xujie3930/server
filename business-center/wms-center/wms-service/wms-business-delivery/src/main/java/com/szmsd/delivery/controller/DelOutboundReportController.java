package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.service.report.IDelOutboundReportService;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = {"出库报表管理"})
@ApiSort(120)
@RestController
@RequestMapping("/api/outbound/report")
public class DelOutboundReportController extends BaseController {

    private final IDelOutboundReportService delOutboundReportService;

    public DelOutboundReportController(IDelOutboundReportService delOutboundReportService) {
        this.delOutboundReportService = delOutboundReportService;
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundReport:create')")
    @PostMapping("/create")
    @ApiOperation(value = "出库报表管理 - 创建统计", position = 100)
    @ApiImplicitParam(name = "dto", value = "查询对象", dataType = "DelOutboundReportQueryDto")
    public R<List<DelOutboundReportListVO>> create(@RequestBody DelOutboundReportQueryDto queryDto) {
        return R.ok(this.delOutboundReportService.queryCreateData(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundReport:bringVerify')")
    @PostMapping("/bringVerify")
    @ApiOperation(value = "出库报表管理 - 提审统计", position = 200)
    @ApiImplicitParam(name = "dto", value = "查询对象", dataType = "DelOutboundReportQueryDto")
    public R<List<DelOutboundReportListVO>> queryBringVerifyData(@RequestBody DelOutboundReportQueryDto queryDto) {
        return R.ok(this.delOutboundReportService.queryBringVerifyData(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundReport:outbound')")
    @PostMapping("/outbound")
    @ApiOperation(value = "出库报表管理 - 出库统计", position = 300)
    @ApiImplicitParam(name = "dto", value = "查询对象", dataType = "DelOutboundReportQueryDto")
    public R<List<DelOutboundReportListVO>> queryOutboundData(@RequestBody DelOutboundReportQueryDto queryDto) {
        return R.ok(this.delOutboundReportService.queryOutboundData(queryDto));
    }

}
