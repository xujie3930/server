package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingDTO;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.service.IHtpWarehouseMappingService;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;


/**
 * <p>
 * 仓库与仓库关联映射 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */


@Api(tags = {"仓库与仓库关联映射"})
@RestController
@RequestMapping("/htpWarehouseMapping")
public class HtpWarehouseMappingController extends BaseController {

    @Resource
    private IHtpWarehouseMappingService htpWarehouseMappingService;


    /**
     * 查询仓库与仓库关联映射模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:list')")
    @PostMapping("/ck1/list")
    @ApiOperation(value = "查询CK1 仓库列表", notes = "查询仓库与仓库关联映射模块列表")
    public TableDataInfo<HtpWarehouseMappingVO> ckList(@RequestBody HtpWarehouseMappingQueryDTO htpWarehouseMapping) {
        return getDataTable(htpWarehouseMappingService.ckList(htpWarehouseMapping));
    }

    /**
     * 查询仓库与仓库关联映射模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:list')")
    @PostMapping("/list")
    @ApiOperation(value = "查询仓库与仓库关联映射模块列表", notes = "查询仓库与仓库关联映射模块列表")
    public TableDataInfo<HtpWarehouseMappingVO> list(@RequestBody HtpWarehouseMappingQueryDTO htpWarehouseMapping) {
        QueryDto queryDto = new QueryDto();
        queryDto.setPageNum(htpWarehouseMapping.getPageNum());
        queryDto.setPageSize(htpWarehouseMapping.getPageSize());
        startPage(queryDto);
        List<HtpWarehouseMappingVO> list = htpWarehouseMappingService.selectHtpWarehouseMappingList(htpWarehouseMapping);
        return getDataTable(list);
    }

    /**
     * 查询仓库与仓库关联映射模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:list')")
    @GetMapping("/getMappingWarCode/{warehouseCode}")
    @ApiOperation(value = "查询映射的仓库编码", notes = "查询映射的仓库编码")
    public R<String> getMappingWarCode(@PathVariable("warehouseCode") @Valid @NotBlank String warehouseCode) {
         return R.ok(htpWarehouseMappingService.getMappingWarCode(warehouseCode));
    }

    /**
     * 获取仓库与仓库关联映射模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取仓库与仓库关联映射模块详细信息", notes = "获取仓库与仓库关联映射模块详细信息")
    public R<HtpWarehouseMappingVO> getInfo(@PathVariable("id") Integer id) {
        return R.ok(htpWarehouseMappingService.selectHtpWarehouseMappingById(id));
    }

    /**
     * 获取仓库与仓库关联映射模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:changeStatus')")
    @PutMapping(value = "changeStatus/{id}/{status}")
    @ApiOperation(value = "修改启用状态", notes = "修改启用状态")
    public R<HtpWarehouseMappingVO> changeStatus(@PathVariable("id") Integer id, @PathVariable("status") Integer status) {
        return R.ok(htpWarehouseMappingService.changeStatus(id, status));
    }

    /**
     * 新增仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:add')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增仓库与仓库关联映射模块", notes = "新增仓库与仓库关联映射模块")
    public R add(@Validated @RequestBody HtpWarehouseMappingDTO htpWarehouseMapping) {
        return toOk(htpWarehouseMappingService.insertHtpWarehouseMapping(htpWarehouseMapping));
    }

    /**
     * 修改仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:edit')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改仓库与仓库关联映射模块", notes = "修改仓库与仓库关联映射模块")
    public R edit(@Validated @RequestBody HtpWarehouseMappingDTO htpWarehouseMapping) {
        return toOk(htpWarehouseMappingService.updateHtpWarehouseMapping(htpWarehouseMapping));
    }

    /**
     * 删除仓库与仓库关联映射模块
     */
    @PreAuthorize("@ss.hasPermi('HtpWarehouseMapping:HtpWarehouseMapping:remove')")
    @Log(title = "仓库与仓库关联映射模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除仓库与仓库关联映射模块", notes = "删除仓库与仓库关联映射模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(htpWarehouseMappingService.deleteHtpWarehouseMappingByIds(ids));
    }

}
