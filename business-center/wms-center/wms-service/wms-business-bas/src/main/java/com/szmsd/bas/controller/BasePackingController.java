package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.*;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.bas.service.IBasePackingService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
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
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-03-06
*/


@Api(tags = {"物料模块"})
@RestController
@RequestMapping("/base/packing")
public class BasePackingController extends BaseController{

     @Resource
     private IBasePackingService basePackingService;
    @Resource
    private IBasSerialNumberService baseSerialNumberService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasePackingQueryDto basePackingQueryDto)
     {
            startPage();
            List<BasePacking> list = basePackingService.selectBasePackingPage(basePackingQueryDto);
            return getDataTable(list);
      }

    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:list')")
    @GetMapping("/listPacking")
    @ApiOperation(value = "查询列表",notes = "查询列表")
    public R listParent()
    {
        List<BasePackingDto> list = basePackingService.selectBasePacking();
        return R.ok(list);
    }
    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasePacking basePacking) throws IOException {
     /*List<BasePacking> list = basePackingService.selectBasePackingList(basePacking);
     ExcelUtil<BasePacking> util = new ExcelUtil<BasePacking>(BasePacking.class);
        util.exportExcel(response,list, "BasePacking");*/

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basePackingService.selectBasePackingById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody BasePacking basePacking)
    {
    return toOk(basePackingService.insertBasePacking(basePacking));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasePacking basePacking) throws IllegalAccessException {
    return toOk(basePackingService.updateBasePacking(basePacking));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<Long> ids)
    {
    return R.ok(basePackingService.deleteBasePackingByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryPackingList')")
    @PostMapping("/queryPackingList")
    @ApiOperation(value = "根据仓库，SKU查询产品信息")
    public R<List<BasePacking>> queryPackingList(@RequestBody BaseProductConditionQueryDto conditionQueryDto) {
        return R.ok(this.basePackingService.queryPackingList(conditionQueryDto));
    }

    @PreAuthorize("@ss.hasPermi('BaseProduct:BaseProduct:queryByCode')")
    @PostMapping("/queryByCode")
    @ApiOperation(value = "根据编码查询包装信息")
    public R<BasePacking> queryByCode(@RequestBody BasePackingConditionQueryDto conditionQueryDto) {
        return R.ok(this.basePackingService.queryByCode(conditionQueryDto));
    }

    @PreAuthorize("@ss.hasPermi('BasePacking:BasePacking:add')")
    @PostMapping("/createPackings")
    @ApiOperation(value = "新增 修改物料", notes = "新增 修改物料")
    public R createPackings(@RequestBody CreatePackingRequest createPackingRequest) {
        this.basePackingService.createPackings(createPackingRequest);
        return R.ok();
    }


}
