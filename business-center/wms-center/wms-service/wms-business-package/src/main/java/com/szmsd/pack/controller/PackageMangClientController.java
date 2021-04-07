package com.szmsd.pack.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.dto.PackageAddressAddDTO;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.service.IPackageMangClientService;
import com.szmsd.pack.vo.PackageAddressVO;
import com.szmsd.pack.vo.PackageMangVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @ClassName: PackageMangClientController
 * @Description: 揽件管理客户端
 * @Author: 11
 * @Date: 2021/4/1 14:02
 */
@Api(tags = {"交货管理-客户端"})
@RestController
@RequestMapping("/client/package")
public class PackageMangClientController extends BaseController {

    @Resource
    private IPackageMangClientService packageMangClientService;

    /**
     * 新增地址
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:add')")
    @Log(title = "新增地址", businessType = BusinessType.INSERT)
    @PostMapping("/address/add")
    @ApiOperation(value = "地址管理-新增地址", notes = "新增地址")
    public R add(@Validated @RequestBody PackageAddressAddDTO packageAddress) {
        return toOk(packageMangClientService.insertPackageAddress(packageAddress));
    }

    /**
     * 地址信息列表
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:list')")
    @GetMapping("/address/list")
    @ApiOperation(value = "地址管理-列表", notes = "地址信息列表")
    public TableDataInfo addressList(@Validated PackageMangQueryDTO packageAddress) {
        startPage();
        List<PackageAddressVO> list = packageMangClientService.selectPackageAddressList(packageAddress);
        return getDataTable(list);
    }

    /**
     * 地址导出
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:export')")
    @Log(title = "交货管理", businessType = BusinessType.EXPORT)
    @GetMapping("/address/export")
    @ApiOperation(value = "地址管理-导出", notes = "导出package - 交货管理 - 地址信息表模块列表")
    public void export(HttpServletResponse response,@Validated PackageMangQueryDTO packageAddress) throws IOException {
        List<PackageAddressVO> list = packageMangClientService.selectPackageAddressList(packageAddress);
        ExcelUtil<PackageAddressVO> util = new ExcelUtil<PackageAddressVO>(PackageAddressVO.class);
        util.exportExcel(response, list, "PackageAddress");

    }

    /**
     * 查询地址详情
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:query')")
    @GetMapping(value = "address/getInfo/{id}")
    @ApiOperation(value = "地址管理-查询地址详情", notes = "获取package - 交货管理 - 地址信息表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageMangClientService.selectPackageAddressById(id));
    }


    /**
     * 修改package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:edit')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @PutMapping("address/edit")
    @ApiOperation(value = "地址管理-修改地址", notes = "修改package - 交货管理 - 地址信息表模块")
    public R edit(@Validated @RequestBody PackageAddressAddDTO packageAddress) {
        return toOk(packageMangClientService.updatePackageAddress(packageAddress));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("address/remove")
    @ApiOperation(value = "地址管理-批量删除地址", notes = "删除package - 交货管理 - 地址信息表模块")
    public R deletePackageAddressByIds(@RequestBody List<String> ids) {
        return toOk(packageMangClientService.deletePackageAddressByIds(ids));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("address/remove/{id}")
    @ApiOperation(value = "地址管理-删除地址", notes = "删除package - 交货管理 - 地址信息表模块")
    public R deletePackageAddressById(@PathVariable(value = "id") String id) {
        return toOk(packageMangClientService.deletePackageAddressById(id));
    }

    /**
     * 设置默认地址
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:update')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @PutMapping("address/setDefaultAddr/{id}")
    @ApiOperation(value = "地址管理-设置默认地址", notes = "设置默认地址当前id未默认地址")
    public R setDefaultAddr(@PathVariable(value = "id") String id) {
        return toOk(packageMangClientService.setDefaultAddr(id));
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:list')")
    @GetMapping("/package/list")
    @ApiOperation(value = "揽件列表-列表", notes = "揽件列表查询")
    public TableDataInfo packageList(@Validated PackageMangQueryDTO packageMangQueryDTO) {
        startPage();
        List<PackageMangVO> list = packageMangClientService.selectPackageManagementList(packageMangQueryDTO);
        return getDataTable(list);
    }


    /**
     * 获取package - 交货管理 - 地址信息表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "交货管理-查询详情", notes = "获取package - 交货管理 - 地址信息表模块详细信息")
    public R selectPackageManagementById(@PathVariable("id") String id) {
        return R.ok(packageMangClientService.selectPackageManagementById(id));
    }

    /**
     * 新增package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:add')")
    @Log(title = "交货管理", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "交货管理-上门揽件【新增】", notes = "交货管理-上门揽件【新增】")
    public R add(@Validated @RequestBody PackageMangAddDTO packageManagement) {
        return toOk(packageMangClientService.insertPackageManagement(packageManagement));
    }

    /**
     * 修改package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:edit')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = "交货管理-编辑", notes = "交货管理-编辑")
    public R edit(@Validated @RequestBody PackageMangAddDTO packageManagement) {
        return toOk(packageMangClientService.updatePackageManagement(packageManagement));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "交货管理-删除揽件", notes = "交货管理-删除揽件")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageMangClientService.deletePackageManagementByIds(ids));
    }

}
