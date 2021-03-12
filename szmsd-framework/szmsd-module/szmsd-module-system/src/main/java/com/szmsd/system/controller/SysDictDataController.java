package com.szmsd.system.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.system.domain.SysDictData;
import com.szmsd.system.service.ISysDictDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 数据字典信息
 *
 * @author lzw
 */
@RestController
@RequestMapping("/dict/data")
@Api(tags = "子类 数据字典信息管理")
public class SysDictDataController extends BaseController {
    @Resource
    private ISysDictDataService dictDataService;

    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取数据字典列表")
    public TableDataInfo list(SysDictData dictData) {
        startPage();
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return getDataTable(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:dict:export')")
    @PostMapping("/export")
    @ApiOperation(httpMethod = "POST", value = "导出字典数据")
    public void export(HttpServletResponse response, SysDictData dictData) throws IOException {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping(value = "/{dictCode}")
    @ApiOperation(httpMethod = "GET", value = "查询字典类型详细")
    public R getInfo(@PathVariable Long dictCode) {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/dictType/{dictType}")
    public R dictType(@PathVariable String dictType) {
        return R.ok(dictDataService.selectDictDataByType(dictType));
    }

    /**
     * 新增字典数据
     */
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(httpMethod = "POST", value = "新增字典数据")
    public R add(@Validated @RequestBody SysDictData dict) {
        dict.setCreateByName(SecurityUtils.getUsername());
        return toOk(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典数据
     */
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(httpMethod = "PUT", value = "修改保存字典数据")
    public R edit(@Validated @RequestBody SysDictData dict) {
        dict.setUpdateByName(SecurityUtils.getUsername());
        return toOk(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典数据
     */
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    @ApiOperation(httpMethod = "DELETE", value = "删除字典类型")
    public R remove(@PathVariable Long[] dictCodes) {
        return toOk(dictDataService.deleteDictDataByIds(dictCodes));
    }

    @PreAuthorize("@ss.hasPermi('system:dict:batchList')")
    @PostMapping("/batchList")
    @ApiOperation(value = "批量查询数据字典")
    public R<Map<String, List<SysDictData>>> batchList(@RequestBody List<String> list) {
        return R.ok(this.dictDataService.batchList(list));
    }
}
