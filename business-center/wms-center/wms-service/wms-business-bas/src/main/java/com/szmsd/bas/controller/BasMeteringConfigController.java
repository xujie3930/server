package com.szmsd.bas.controller;


import com.szmsd.bas.domain.BasMeteringConfig;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.bas.service.IBasMeteringConfigService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"计泡规则"})
@RestController
@RequestMapping("/bas/basMeteringConfigController")
public class BasMeteringConfigController extends BaseController {
    @Resource
    private IBasMeteringConfigService iBasMeteringConfigService;

    //@PreAuthorize("@ss.hasPermi('bas:basMeteringConfigController:page')")
    @PostMapping("/page")
    @ApiOperation(value = "查询", notes = "计泡查询 - 分页查询")
    public TableDataInfo<BasMeteringConfig> page(@RequestBody BasMeteringConfigDto basMeteringConfigDto) {
        startPage();
        List<BasMeteringConfig> list = iBasMeteringConfigService.selectList(basMeteringConfigDto);
        return getDataTable(list);
    }

    @PostMapping("/insertBasMeteringConfig")
    @ApiOperation(value = "新增", notes = "计泡新增")
    public R insertBasMeteringConfig(@RequestBody  BasMeteringConfig BasMeteringConfig) {

        R r= iBasMeteringConfigService.insertBasMeteringConfig(BasMeteringConfig);
        return r;
    }

    @PostMapping("/UpdateBasMeteringConfig")
    @ApiOperation(value = "修改", notes = "计泡修改")
    public R UpdateBasMeteringConfig(@RequestBody  BasMeteringConfig BasMeteringConfig) {

        R r= iBasMeteringConfigService.UpdateBasMeteringConfig(BasMeteringConfig);
        return r;
    }

    @GetMapping("/selectById")
    @ApiOperation(value = "根据id查询详情计泡详情", notes = "根据id查询详情计泡详情")
    public R selectById(Integer id) {
        R r= iBasMeteringConfigService.selectById(id);
        return r;
    }

    @PostMapping("/intercept")
    @ApiOperation(value = "计泡提供接口 做出库拦截", notes = "做出库拦截")
    public R intercept(@RequestBody BasMeteringConfigDto basMeteringConfigDto) {
        R r= iBasMeteringConfigService.intercept(basMeteringConfigDto);
        return r;
    }


}
