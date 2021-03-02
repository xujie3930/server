package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasE3Mes;
import com.szmsd.bas.service.IBasE3MesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * E3消息表 前端控制器
 * </p>
 *
 * @author admin
 * @since 2020-11-28
 */


@Api(tags = {"E3消息表"})
@RestController
@RequestMapping("/bas-e3-mes")
public class BasE3MesController extends BaseController {

    @Resource
    private IBasE3MesService basE3MesService;
    /**
     * 查询E3消息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询E3消息表模块列表",notes = "查询E3消息表模块列表")
    public TableDataInfo list(BasE3Mes basE3Mes)
    {
        startPage();
        List<BasE3Mes> list = basE3MesService.selectBasE3MesList(basE3Mes);
        return getDataTable(list);
    }

    /**
     * 导出E3消息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:export')")
    @Log(title = "E3消息表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出E3消息表模块列表",notes = "导出E3消息表模块列表")
    public void export(HttpServletResponse response, BasE3Mes basE3Mes) throws IOException {
        List<BasE3Mes> list = basE3MesService.selectBasE3MesList(basE3Mes);
        ExcelUtil<BasE3Mes> util = new ExcelUtil<BasE3Mes>(BasE3Mes.class);
        util.exportExcel(response,list, "BasE3Mes");

    }

    /**
     * 获取E3消息表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取E3消息表模块详细信息",notes = "获取E3消息表模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
        return R.ok(basE3MesService.selectBasE3MesById(id));
    }

    /**
     * 新增E3消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:add')")
    @Log(title = "E3消息表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增E3消息表模块",notes = "新增E3消息表模块")
    public R add(@RequestBody BasE3Mes basE3Mes)
    {
        return toOk(basE3MesService.insertBasE3Mes(basE3Mes));
    }

    /**
     * 修改E3消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:edit')")
    @Log(title = "E3消息表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改E3消息表模块",notes = "修改E3消息表模块")
    public R edit(@RequestBody BasE3Mes basE3Mes)
    {
        return toOk(basE3MesService.updateBasE3Mes(basE3Mes));
    }

    /**
     * 删除E3消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasE3Mes:BasE3Mes:batchDel')")
    @Log(title = "E3消息表模块", businessType = BusinessType.UPDATE)
    @PutMapping("batchDel")
    @ApiOperation(value = "批量删除E3消息表模块",notes = "批量删除E3消息表模块")
    public R batchDel(@RequestBody List<String> idList)
    {
        return toOk(basE3MesService.batchDel(idList));
    }

}
