package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenancePageRequest;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;
import com.szmsd.http.service.IHttpCustomPricesService;
import com.szmsd.http.service.IHttpDiscountService;
import com.szmsd.http.service.IHttpGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = {"等级方案"})
@RestController
@RequestMapping("/api/grade/http")
public class HttpGradeController extends BaseController {

    @Resource
    private IHttpGradeService httpGradeService;


    @PostMapping("/page")
    @ApiOperation(value = "分页查询等级方案")
    public R<PageVO> page(@RequestBody GradePageRequest pageDTO) {
        return httpGradeService.page(pageDTO);
    }


    @PostMapping("/detailResult")
    @ApiOperation(value = "获取等级方案明细信息")
    public R detailResult(@RequestBody String id) {
        return httpGradeService.detailResult(id);
    }


    @PostMapping("/create")
    @ApiOperation(value = "创建等级方案")
    public R create(@RequestBody MergeGradeDto dto) {
        return httpGradeService.create(dto);
    }


    @PostMapping("/update")
    @ApiOperation(value = "修改等级方案")
    public R update(@RequestBody MergeGradeDto dto) {
        return httpGradeService.update(dto);
    }

    @PostMapping("/detailImport")
    @ApiOperation(value = "修改等级方案关联产品")
    public R detailImport(@RequestBody UpdateGradeDetailDto dto) {
        return httpGradeService.detailImport(dto);
    }

    @PostMapping("/customUpdate")
    @ApiOperation(value = "修改等级方案关联客户")
    public R customUpdate(@RequestBody UpdateGradeCustomDto dto) {
        return httpGradeService.customUpdate(dto);
    }

}
