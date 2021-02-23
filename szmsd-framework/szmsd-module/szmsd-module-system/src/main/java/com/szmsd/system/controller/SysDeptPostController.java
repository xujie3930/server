package com.szmsd.system.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.szmsd.system.service.ISysDeptPostService;
import com.szmsd.system.domain.SysDeptPost;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

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
 * 机构部门与岗位关联 前端控制器
 * </p>
 *
 * @author lzw
 * @since 2020-07-06
 */


@Api(tags = {"机构部门与岗位关联表"})
@RestController
@RequestMapping("/dept/post")
public class SysDeptPostController extends BaseController {

    @Resource
    private ISysDeptPostService sysDeptPostService;

    /**
     * 查询机构部门与岗位关联表模块列表
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询机构部门与岗位关联表模块列表", notes = "查询机构部门与岗位关联表模块列表")
    public R list(SysDeptPost sysDeptPost) {
        startPage();
        List<SysDeptPost> list = sysDeptPostService.selectSysDeptPostList(sysDeptPost);
        return R.ok(list);
    }

//    /**
//    * 导出机构部门与岗位关联表模块列表
//    */
//     @PreAuthorize("@ss.hasPermi('system:dept:export')")
//     @Log(title = "机构部门与岗位关联表模块", businessType = BusinessType.EXPORT)
//     @GetMapping("/export")
//     @ApiOperation(value = "导出机构部门与岗位关联表模块列表",notes = "导出机构部门与岗位关联表模块列表")
//     public void export(HttpServletResponse response, SysDeptPost sysDeptPost) throws IOException {
//     List<SysDeptPost> list = sysDeptPostService.selectSysDeptPostList(sysDeptPost);
//     ExcelUtil<SysDeptPost> util = new ExcelUtil<SysDeptPost>(SysDeptPost.class);
//        util.exportExcel(response,list, "SysDeptPost");
//
//     }

//    /**
//     * 获取机构部门与岗位关联表模块详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('system:dept:query')")
//    @GetMapping(value = "getInfo/{id}")
//    @ApiOperation(value = "获取机构部门与岗位关联表模块详细信息", notes = "获取机构部门与岗位关联表模块详细信息")
//    public R getInfo(@PathVariable("id") String id) {
//        return R.ok(sysDeptPostService.selectSysDeptPostById(id));
//    }

    /**
     * 新增机构部门与岗位关联表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "机构部门与岗位关联表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增机构部门与岗位关联表模块", notes = "新增机构部门与岗位关联表模块")
    public R add(@Validated @RequestBody SysDeptPost sysDeptPost) {
        return sysDeptPostService.insertSysDeptPost(sysDeptPost);
    }

    /**
     * 修改机构部门与岗位关联表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "机构部门与岗位关联表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改机构部门与岗位关联表模块", notes = "修改机构部门与岗位关联表模块")
    public R edit(@Validated @RequestBody SysDeptPost sysDeptPost) {
        return sysDeptPostService.updateSysDeptPost(sysDeptPost);
    }

    /**
     * 删除机构部门与岗位关联表模块
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "机构部门与岗位关联表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除机构部门与岗位关联表模块", notes = "删除机构部门与岗位关联表模块")
    public R remove(@Validated @RequestBody SysDeptPost sysDeptPost) {
        return toOk(sysDeptPostService.deleteSysDeptPostByDeptPost(sysDeptPost));
    }

}
