package com.szmsd.pack.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.pack.dto.PackageAddressAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.service.IPackageMangClientService;
import com.szmsd.pack.vo.PackageAddressVO;
import com.szmsd.pack.vo.PackageMangVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private IPackageMangClientService packageAddressService;

    /**
     * 新增地址
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:add')")
    @Log(title = "新增地址", businessType = BusinessType.INSERT)
    @PostMapping("/address/add")
    @ApiOperation(value = "新增地址", notes = "新增地址")
    public R add(@RequestBody PackageAddressAddDTO packageAddress) {
        return toOk(packageAddressService.insertPackageAddress(packageAddress));
    }

    /**
     * 地址信息列表
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:list')")
    @GetMapping("/address/list")
    @ApiOperation(value = "地址信息表模块列表", notes = "地址信息列表")
    public TableDataInfo addressList(PackageMangQueryDTO packageAddress) {
        startPage();
        List<PackageAddressVO> list = packageAddressService.selectPackageAddressList(packageAddress);
        return getDataTable(list);
    }

    /**
     * 地址导出
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:export')")
    @Log(title = "交货管理", businessType = BusinessType.EXPORT)
    @GetMapping("/address/export")
    @ApiOperation(value = "地址导出", notes = "导出package - 交货管理 - 地址信息表模块列表")
    public void export(HttpServletResponse response, PackageMangQueryDTO packageAddress) throws IOException {
        List<PackageAddressVO> list = packageAddressService.selectPackageAddressList(packageAddress);
        ExcelUtil<PackageAddressVO> util = new ExcelUtil<PackageAddressVO>(PackageAddressVO.class);
        util.exportExcel(response, list, "PackageAddress");

    }

    /**
     * 查询地址详情
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:query')")
    @GetMapping(value = "address/getInfo/{id}")
    @ApiOperation(value = "查询地址详情", notes = "获取package - 交货管理 - 地址信息表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageAddressService.selectPackageAddressById(id));
    }


    /**
     * 修改package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:edit')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @PutMapping("address/edit")
    @ApiOperation(value = "修改地址", notes = "修改package - 交货管理 - 地址信息表模块")
    public R edit(@RequestBody PackageAddressAddDTO packageAddress) {
        return toOk(packageAddressService.updatePackageAddress(packageAddress));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("address/remove")
    @ApiOperation(value = "批量删除地址", notes = "删除package - 交货管理 - 地址信息表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageAddressService.deletePackageAddressByIds(ids));
    }

    /**
     * 删除package - 交货管理 - 地址信息表模块
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:remove')")
    @Log(title = "交货管理", businessType = BusinessType.DELETE)
    @DeleteMapping("address/remove/{id}")
    @ApiOperation(value = "删除地址", notes = "删除package - 交货管理 - 地址信息表模块")
    public R remove(@PathVariable(value = "id") String id) {
        return toOk(packageAddressService.deletePackageAddressById(id));
    }

    /**
     * 设置默认地址
     */
    @PreAuthorize("@ss.hasPermi('PackageAddress:PackageAddress:update')")
    @Log(title = "交货管理", businessType = BusinessType.UPDATE)
    @DeleteMapping("address/setDefaultAddr/{id}")
    @ApiOperation(value = "设置默认地址", notes = "设置默认地址当前id未默认地址")
    public R setDefaultAddr(@PathVariable(value = "id") String id) {
        return toOk(packageAddressService.setDefaultAddr(id));
    }

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     */
    @PreAuthorize("@ss.hasPermi('PackageManagement:PackageManagement:list')")
    @GetMapping("/package/list")
    @ApiOperation(value = "揽件列表", notes = "揽件列表查询")
    public TableDataInfo packageList(PackageMangQueryDTO packageMangQueryDTO) {
        startPage();
        List<PackageMangVO> list = packageAddressService.selectPackageManagementList(packageMangQueryDTO);
        return getDataTable(list);
    }

}
