package com.szmsd.pack.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.http.api.service.IHtpPricedProductClientService;
import com.szmsd.http.dto.PricedProductInServiceCriteria;
import com.szmsd.http.vo.PricedProduct;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.dto.PackageCollectionQueryDto;
import com.szmsd.pack.service.IPackageCollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * package - 交货管理 - 揽收 前端控制器
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Api(tags = {"交货管理 - 揽收"})
@RestController
@RequestMapping("/package-collection")
public class PackageCollectionController extends BaseController {

    @Resource
    private IPackageCollectionService packageCollectionService;
    @Resource
    private IHtpPricedProductClientService htpPricedProductClientService;

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:list')")
    @PostMapping("/list")
    @ApiOperation(value = "交货管理 - 揽收 - 列表", notes = "交货管理 - 揽收 - 列表")
    @ApiImplicitParam(name = "dto", value = "参数", dataType = "PackageCollectionQueryDto")
    @AutoValue
    public R<IPage<PackageCollection>> list(@RequestBody PackageCollectionQueryDto dto) {
        IPage<PackageCollection> page = this.packageCollectionService.page(dto);
        return R.ok(page);
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "交货管理 - 揽收 - 详细信息", notes = "交货管理 - 揽收 - 详细信息")
    @AutoValue
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(packageCollectionService.selectPackageCollectionById(id));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:add')")
    @Log(title = "package - 交货管理 - 揽收模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "交货管理 - 揽收 - 新增", notes = "交货管理 - 揽收 - 新增")
    public R add(@RequestBody PackageCollection packageCollection) {
        return toOk(packageCollectionService.insertPackageCollection(packageCollection));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:edit')")
    @Log(title = "package - 交货管理 - 揽收模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = "交货管理 - 揽收 - 修改", notes = "交货管理 - 揽收 - 修改")
    public R edit(@RequestBody PackageCollection packageCollection) {
        return toOk(packageCollectionService.updatePackageCollection(packageCollection));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:editPlan')")
    @Log(title = "package - 交货管理 - 揽收模块", businessType = BusinessType.UPDATE)
    @PutMapping("editPlan")
    @ApiOperation(value = "交货管理 - 揽收 - 创建提货计划", notes = "交货管理 - 揽收 - 创建提货计划")
    public R editPlan(@RequestBody PackageCollection packageCollection) {
        return toOk(packageCollectionService.updatePackageCollectionPlan(packageCollection));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:cancel')")
    @Log(title = "package - 交货管理 - 揽收模块", businessType = BusinessType.UPDATE)
    @PostMapping("cancel")
    @ApiOperation(value = "交货管理 - 揽收 - 取消", notes = "交货管理 - 揽收 - 取消")
    public R cancel(@RequestBody List<Long> idList) {
        return toOk(packageCollectionService.cancel(idList));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:remove')")
    @Log(title = "package - 交货管理 - 揽收模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "交货管理 - 揽收 - 删除", notes = "交货管理 - 揽收 - 删除")
    public R remove(@RequestBody List<String> ids) {
        return toOk(packageCollectionService.deletePackageCollectionByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:inService')")
    @PostMapping("/inService")
    @ApiOperation(value = "交货管理 - 揽收 - 物流服务", notes = "交货管理 - 揽收 - 物流服务", position = 100)
    @ApiImplicitParam(name = "criteria", value = "参数", dataType = "PricedProductInServiceCriteria")
    public R<List<PricedProduct>> inService(@RequestBody PricedProductInServiceCriteria criteria) {
        return R.ok(this.htpPricedProductClientService.inService(criteria));
    }

    @PreAuthorize("@ss.hasPermi('PackageCollection:PackageCollection:updateCollecting')")
    @PostMapping("/updateCollecting")
    @ApiOperation(value = "交货管理 - 揽收 - 修改状态为揽收中", notes = "交货管理 - 揽收 - 修改状态为揽收中", position = 110)
    @ApiImplicitParam(name = "collectionNo", value = "参数", dataType = "String")
    public R<Integer> updateCollecting(@RequestBody String collectionNo) {
        return R.ok(this.packageCollectionService.updateCollecting(collectionNo));
    }
}
