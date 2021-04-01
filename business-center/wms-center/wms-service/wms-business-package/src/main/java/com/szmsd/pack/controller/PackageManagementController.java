package com.szmsd.pack.controller;


import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.domain.PackageManagement;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.service.IPackageManagementService;
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
 * package - 交货管理 - 地址信息表 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */


@Api(tags = {"交货管理-揽收"})
@RestController
@RequestMapping("/service/package/management")
public class PackageManagementController extends BaseController {

    @Resource
    private IPackageManagementService packageManagementService;

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:list')")
    @GetMapping("/list")
    @ApiOperation(value = "交货管理", notes = "查询package - 交货管理 - 地址信息表模块列表")
    public TableDataInfo list(PackageMangQueryDTO packageMangQueryDTO) {
        startPage();
        List<PackageManagement> list = packageManagementService.selectPackageManagementList(packageMangQueryDTO);
        return getDataTable(list);
    }

    /**
     * 导出package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:export')")
    @Log(title = "交货管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出package - 交货管理 - 地址信息表模块列表", notes = "导出package - 交货管理 - 地址信息表模块列表")
    public void export(HttpServletResponse response, PackageMangQueryDTO packageManagement) throws IOException {
        List<PackageManagement> list = packageManagementService.selectPackageManagementList(packageManagement);
        ExcelUtil<PackageManagement> util = new ExcelUtil<PackageManagement>(PackageManagement.class);
        util.exportExcel(response, list, "PackageManagement");

    }

    /**
     * 获取package - 交货管理 - 地址信息表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取package - 交货管理 - 地址信息表模块详细信息", notes = "获取package - 交货管理 - 地址信息表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageManagementService.selectPackageManagementById(id));
    }

    /**
     * 新增package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:add')")
    @Log(title = "package - 交货管理 - 地址信息表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增package - 交货管理 - 地址信息表模块", notes = "新增package - 交货管理 - 地址信息表模块")
    public R add(@RequestBody PackageManagement packageManagement) {
        return toOk(packageManagementService.insertPackageManagement(packageManagement));
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:edit')")
    @Log(title = "package - 交货管理 - 地址信息表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改package - 交货管理 - 地址信息表模块", notes = "修改package - 交货管理 - 地址信息表模块")
    public R edit(@RequestBody PackageManagement packageManagement) {
        return toOk(packageManagementService.updatePackageManagement(packageManagement));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:remove')")
    @Log(title = "package - 交货管理 - 地址信息表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除package - 交货管理 - 地址信息表模块", notes = "删除package - 交货管理 - 地址信息表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageManagementService.deletePackageManagementByIds(ids));
    }

}
