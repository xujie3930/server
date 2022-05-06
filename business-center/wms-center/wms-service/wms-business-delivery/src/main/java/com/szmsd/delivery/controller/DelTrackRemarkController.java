package com.szmsd.delivery.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelTrackRemarkService;
import com.szmsd.delivery.domain.DelTrackRemark;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.util.List;
import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
 * <p>
 * 轨迹备注表 前端控制器
 * </p>
 *
 * @author YM
 * @since 2022-05-06
 */


@Api(tags = {"轨迹备注表"})
@RestController
@RequestMapping("/del-track-remark")
public class DelTrackRemarkController extends BaseController {

    @Resource
    private IDelTrackRemarkService delTrackRemarkService;

    /**
     * 查询轨迹备注表模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询轨迹备注表模块列表", notes = "查询轨迹备注表模块列表")
    public TableDataInfo list(DelTrackRemark delTrackRemark) {
        startPage();
        List<DelTrackRemark> list = delTrackRemarkService.selectDelTrackRemarkList(delTrackRemark);
        return getDataTable(list);
    }

    /**
     * 导出轨迹备注表模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:export')")
    @Log(title = "轨迹备注表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出轨迹备注表模块列表", notes = "导出轨迹备注表模块列表")
    public void export(HttpServletResponse response, DelTrackRemark delTrackRemark) throws IOException {
        List<DelTrackRemark> list = delTrackRemarkService.selectDelTrackRemarkList(delTrackRemark);
        ExcelUtil<DelTrackRemark> util = new ExcelUtil<DelTrackRemark>(DelTrackRemark.class);
        util.exportExcel(response, list, "DelTrackRemark");

    }

    /**
     * 获取轨迹备注表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取轨迹备注表模块详细信息", notes = "获取轨迹备注表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(delTrackRemarkService.selectDelTrackRemarkById(id));
    }

    /**
     * 新增轨迹备注表模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:add')")
    @Log(title = "轨迹备注表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增轨迹备注表模块", notes = "新增轨迹备注表模块")
    public R add(@RequestBody @Valid DelTrackRemark delTrackRemark) {
        return toOk(delTrackRemarkService.insertDelTrackRemark(delTrackRemark));
    }

    /**
     * 修改轨迹备注表模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:edit')")
    @Log(title = "轨迹备注表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改轨迹备注表模块", notes = "修改轨迹备注表模块")
    public R edit(@RequestBody @Valid DelTrackRemark delTrackRemark) {
        return toOk(delTrackRemarkService.updateDelTrackRemark(delTrackRemark));
    }

    /**
     * 删除轨迹备注表模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrackRemark:DelTrackRemark:remove')")
    @Log(title = "轨迹备注表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除轨迹备注表模块", notes = "删除轨迹备注表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delTrackRemarkService.deleteDelTrackRemarkByIds(ids));
    }

}
