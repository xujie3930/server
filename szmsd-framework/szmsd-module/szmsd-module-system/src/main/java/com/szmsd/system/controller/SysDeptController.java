package com.szmsd.system.controller;

import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.system.api.domain.SysDept;
import com.szmsd.system.api.domain.dto.SysDeptDto;
import com.szmsd.system.service.ISysDeptService;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import com.szmsd.common.log.annotation.Log;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

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
 * 部门表 前端控制器
 * </p>
 *
 * @author lzw
 * @since 2020-07-01
 */


@Api(tags = {"部门表"})
@RestController
@RequestMapping("/dept")
public class SysDeptController extends BaseController {

    @Resource
    private ISysDeptService sysDeptService;

    /**
     * 查询部门表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询部门表模块列表", notes = "查询部门表模块列表")
    public R list(SysDeptDto sysDeptDTO) {
        SysDept sysDept = new SysDept();
        BeanUtils.copyBeanProp(sysDept, sysDeptDTO);
//            startPage();
        List<SysDept> list = sysDeptService.selectSysDeptList(sysDept);
        return R.ok(sysDeptService.buildDeptTreeSelect(list));
    }

    /**
     * 导出部门表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:export')")
    @Log(title = "部门表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出部门表模块列表", notes = "导出部门表模块列表")
    public void export(HttpServletResponse response, SysDeptDto sysDeptDTO) throws IOException {
        SysDept sysDept = new SysDept();
        BeanUtils.copyBeanProp(sysDept, sysDeptDTO);
        List<SysDept> list = sysDeptService.selectSysDeptList(sysDept);
        ExcelUtil<SysDept> util = new ExcelUtil<SysDept>(SysDept.class);
        util.exportExcel(response, list, "SysDept");

    }

    /**
     * 获取部门表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取部门表模块详细信息", notes = "获取部门表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(sysDeptService.selectSysDeptById(id));
    }

    /**
     * 新增部门表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "部门表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增部门表模块", notes = "新增部门表模块")
    public R add(@RequestBody SysDeptDto sysDeptDTO) {
        SysDept sysDept = new SysDept();
        BeanUtils.copyBeanProp(sysDept, sysDeptDTO);
        return toOk(sysDeptService.insertSysDept(sysDept));
    }

    /**
     * 修改部门表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "部门表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改部门表模块", notes = "修改部门表模块")
    public R edit(@RequestBody SysDeptDto sysDeptDTO) {
        SysDept sysDept=new SysDept();
        BeanUtils.copyBeanProp(sysDept,sysDeptDTO);
        return toOk(sysDeptService.updateSysDept(sysDept));
    }

    /**
     * 删除部门表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "部门表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove/{ids}")
    @ApiOperation(value = "删除部门表模块", notes = "删除部门表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(sysDeptService.deleteSysDeptByIds(ids));
    }




}
