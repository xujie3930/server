package com.szmsd.bas.controller;


import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasRegionSelectListQueryDto;
import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.service.IBasRegionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * 地区信息 前端控制器
 * </p>
 *
 * @author gen
 * @since 2020-11-13
 */
@Api(tags = {"地区信息"})
@RestController
@RequestMapping("/bas-region")
public class BasRegionController extends BaseController {

    @Autowired
    private IBasRegionService basRegionService;

    /**
     * 查询地区信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询地区信息模块列表", notes = "查询地区信息模块列表")
    public TableDataInfo list(BasRegion basRegion) {
        startPage();
        List<BasRegion> list = basRegionService.selectBasRegionList(basRegion);
        return getDataTable(list);
    }

    /**
     * 查询地区信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:listCountry')")
    @GetMapping("/listCountry")
    @ApiOperation(value = "查询国家信息模块列表", notes = "查询国家信息模块列表")
    public R<List<BasRegion>> listCountry() {

        List<BasRegion> list = basRegionService.listCountry();
        return R.ok(list);
    }

    /**
     * 导出地区信息模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:export')")
    @Log(title = "地区信息模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出地区信息模块列表", notes = "导出地区信息模块列表")
    public void export(HttpServletResponse response, BasRegion basRegion) throws IOException {
        List<BasRegion> list = basRegionService.selectBasRegionList(basRegion);
        ExcelUtil<BasRegion> util = new ExcelUtil<BasRegion>(BasRegion.class);
        util.exportExcel(response, list, "BasRegion");
    }

    /**
     * 获取地区信息模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取地区信息模块详细信息", notes = "获取地区信息模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basRegionService.selectBasRegionById(id));
    }

    /**
     * 新增地区信息模块
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:add')")
    @Log(title = "地区信息模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增地区信息模块", notes = "新增地区信息模块")
    public R add(@RequestBody BasRegion basRegion) {
        return toOk(basRegionService.insertBasRegion(basRegion));
    }

    /**
     * 修改地区信息模块
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:edit')")
    @Log(title = "地区信息模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改地区信息模块", notes = "修改地区信息模块")
    public R edit(@RequestBody BasRegion basRegion) {
        return toOk(basRegionService.updateBasRegion(basRegion));
    }

    /**
     * 删除地区信息模块
     */
    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:remove')")
    @Log(title = "地区信息模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除地区信息模块", notes = "删除地区信息模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basRegionService.deleteBasRegionByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:countryList')")
    @GetMapping("/countryList")
    @ApiOperation(value = "基础资料 - 地区信息 - 国家列表", notes = "基础资料 - 地区信息 - 国家列表")
    @ApiImplicitParam(name = "queryDto", value = "查询对象", dataType = "BasRegionSelectListQueryDto")
    public R<List<BasRegionSelectListVO>> countryList(BasRegionSelectListQueryDto queryDto) {
        queryDto.setType(1);
        return R.ok(basRegionService.selectList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:country')")
    @GetMapping("/queryByCountryCode")
    @ApiOperation(value = "基础资料 - 地区信息 - 国家", notes = "基础资料 - 地区信息 - 国家")
    public R<BasRegionSelectListVO> queryByCountryCode(@RequestParam("addressCode") String addressCode) {
        List<BasRegionSelectListVO> data = basRegionService.selectList(new BasRegionSelectListQueryDto().setType(1).setAddressCode(addressCode));
        return CollectionUtils.isEmpty(data) ? R.ok() : R.ok(data.get(0));
    }

    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:provinceList')")
    @GetMapping("/provinceList")
    @ApiOperation(value = "基础资料 - 地区信息 - 省份列表", notes = "基础资料 - 地区信息 - 省份列表")
    @ApiImplicitParam(name = "queryDto", value = "查询对象", dataType = "BasRegionSelectListQueryDto")
    public R<List<BasRegionSelectListVO>> provinceList(BasRegionSelectListQueryDto queryDto) {
        queryDto.setType(2);
        return R.ok(basRegionService.selectList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:cityList')")
    @GetMapping("/cityList")
    @ApiOperation(value = "基础资料 - 地区信息 - 城市列表", notes = "基础资料 - 地区信息 - 城市列表")
    @ApiImplicitParam(name = "queryDto", value = "查询对象", dataType = "BasRegionSelectListQueryDto")
    public R<List<BasRegionSelectListVO>> cityList(BasRegionSelectListQueryDto queryDto) {
        queryDto.setType(3);
        return R.ok(basRegionService.selectList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('BasRegion:BasRegion:countyList')")
    @GetMapping("/countyList")
    @ApiOperation(value = "基础资料 - 地区信息 - 区县列表", notes = "基础资料 - 地区信息 - 区县列表")
    @ApiImplicitParam(name = "queryDto", value = "查询对象", dataType = "BasRegionSelectListQueryDto")
    public R<List<BasRegionSelectListVO>> countyList(BasRegionSelectListQueryDto queryDto) {
        queryDto.setType(4);
        return R.ok(basRegionService.selectList(queryDto));
    }
}
