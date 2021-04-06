package com.szmsd.pack.controller;


import com.alibaba.nacos.common.utils.CollectionUtils;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.service.IPackageMangServeService;
import com.szmsd.pack.vo.PackageMangVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
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
    private IPackageMangServeService packageManagementService;

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:list')")
    @GetMapping("/list")
    @ApiOperation(value = "交货管理-揽收列表", notes = "查询package - 交货管理 - 地址信息表模块列表")
    public TableDataInfo list(@Validated PackageMangQueryDTO packageMangQueryDTO) {
        startPage();
        List<PackageMangVO> list = packageManagementService.selectPackageManagementList(packageMangQueryDTO);
        return getDataTable(list);
    }

    /**
     * 导出package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:export')")
    @Log(title = "交货管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "交货管理-揽收列表导出", notes = "导出package - 交货管理 - 地址信息表模块列表")
    public void export(HttpServletResponse response,@Validated PackageMangQueryDTO packageManagement) throws IOException {
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(packageManagement.getIds()), "请选择导出的数据列");
        List<PackageMangVO> list = packageManagementService.selectPackageManagementList(packageManagement);
        packageManagementService.setExportStatus(packageManagement.getIds());
        ExcelUtil<PackageMangVO> util = new ExcelUtil<PackageMangVO>(PackageMangVO.class);
        util.exportExcel(response, list, "PackageManagement-" + LocalDate.now());

    }

    /**
     * 获取package - 交货管理 - 地址信息表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "交货管理-查询详情", notes = "获取package - 交货管理 - 地址信息表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageManagementService.selectPackageManagementById(id));
    }

    /**
     * 新增package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:add')")
    @Log(title = "交货管理", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "交货管理-上门揽件【新增】", notes = "交货管理-上门揽件【新增】")
    public R add(@Validated @RequestBody PackageMangAddDTO packageManagement) {
        return toOk(packageManagementService.insertPackageManagement(packageManagement));
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:edit')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = "交货管理-编辑", notes = "交货管理-编辑")
    public R edit(@Validated @RequestBody PackageMangAddDTO packageManagement) {
        return toOk(packageManagementService.updatePackageManagement(packageManagement));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "交货管理-删除揽件", notes = "交货管理-删除揽件")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageManagementService.deletePackageManagementByIds(ids));
    }

}
