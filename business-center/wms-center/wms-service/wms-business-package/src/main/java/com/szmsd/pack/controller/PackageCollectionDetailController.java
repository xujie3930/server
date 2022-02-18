package com.szmsd.pack.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.domain.PackageCollectionDetail;
import com.szmsd.pack.service.IPackageCollectionDetailService;
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
 * package - 交货管理 - 揽收货物 前端控制器
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Api(tags = {"package - 交货管理 - 揽收货物"})
@RestController
@RequestMapping("/package-collection-detail")
public class PackageCollectionDetailController extends BaseController {

    @Resource
    private IPackageCollectionDetailService packageCollectionDetailService;

    /**
     * 查询package - 交货管理 - 揽收货物模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询package - 交货管理 - 揽收货物模块列表", notes = "查询package - 交货管理 - 揽收货物模块列表")
    public TableDataInfo list(PackageCollectionDetail packageCollectionDetail) {
        startPage();
        List<PackageCollectionDetail> list = packageCollectionDetailService.selectPackageCollectionDetailList(packageCollectionDetail);
        return getDataTable(list);
    }

    /**
     * 导出package - 交货管理 - 揽收货物模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:export')")
    @Log(title = "package - 交货管理 - 揽收货物模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出package - 交货管理 - 揽收货物模块列表", notes = "导出package - 交货管理 - 揽收货物模块列表")
    public void export(HttpServletResponse response, PackageCollectionDetail packageCollectionDetail) throws IOException {
        List<PackageCollectionDetail> list = packageCollectionDetailService.selectPackageCollectionDetailList(packageCollectionDetail);
        ExcelUtil<PackageCollectionDetail> util = new ExcelUtil<PackageCollectionDetail>(PackageCollectionDetail.class);
        util.exportExcel(response, list, "PackageCollectionDetail");

    }

    /**
     * 获取package - 交货管理 - 揽收货物模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取package - 交货管理 - 揽收货物模块详细信息", notes = "获取package - 交货管理 - 揽收货物模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageCollectionDetailService.selectPackageCollectionDetailById(id));
    }

    /**
     * 新增package - 交货管理 - 揽收货物模块
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:add')")
    @Log(title = "package - 交货管理 - 揽收货物模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增package - 交货管理 - 揽收货物模块", notes = "新增package - 交货管理 - 揽收货物模块")
    public R add(@RequestBody PackageCollectionDetail packageCollectionDetail) {
        return toOk(packageCollectionDetailService.insertPackageCollectionDetail(packageCollectionDetail));
    }

    /**
     * 修改package - 交货管理 - 揽收货物模块
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:edit')")
    @Log(title = "package - 交货管理 - 揽收货物模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改package - 交货管理 - 揽收货物模块", notes = "修改package - 交货管理 - 揽收货物模块")
    public R edit(@RequestBody PackageCollectionDetail packageCollectionDetail) {
        return toOk(packageCollectionDetailService.updatePackageCollectionDetail(packageCollectionDetail));
    }

    /**
     * 删除package - 交货管理 - 揽收货物模块
     */
    @PreAuthorize("@ss.hasPermi('PackageCollectionDetail:PackageCollectionDetail:remove')")
    @Log(title = "package - 交货管理 - 揽收货物模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除package - 交货管理 - 揽收货物模块", notes = "删除package - 交货管理 - 揽收货物模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageCollectionDetailService.deletePackageCollectionDetailByIds(ids));
    }

}
