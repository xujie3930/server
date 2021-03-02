package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasArea;
import com.szmsd.bas.service.IBasAreaService;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-07-04
 */

@Api(tags = {"国家省市区四级联动"})
@RestController
@RequestMapping("/bas-area")
public class BasAreaController extends BaseController {


    @Resource
    private IBasAreaService basAreaService;

    /**
     * 查询模块列表
     */
    @ApiOperation(value = "查询区县", notes = "查询区县('bas:basarea:list')")
    @PreAuthorize("@ss.hasPermi('bas:basarea:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasArea basArea) {
        List<BasArea> list = basAreaService.selectBasAreaList(basArea);
        return getDataTable(list);
    }



}
