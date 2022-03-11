package com.szmsd.bas.controller;

import com.szmsd.bas.event.KeywordSyncEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasCarrierKeywordService;
import com.szmsd.bas.domain.BasCarrierKeyword;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.util.List;
import java.io.IOException;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YM
 * @since 2022-01-24
 */


@Api(tags = {"关键词维护"})
@RestController
@RequestMapping("/bas-carrier-keyword")
public class BasCarrierKeywordController extends BaseController {

    @Resource
    private IBasCarrierKeywordService basCarrierKeywordService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    public TableDataInfo list(BasCarrierKeyword basCarrierKeyword) {
        startPage();
        List<BasCarrierKeyword> list = basCarrierKeywordService.selectBasCarrierKeywordList(basCarrierKeyword);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, BasCarrierKeyword basCarrierKeyword) throws IOException {
        List<BasCarrierKeyword> list = basCarrierKeywordService.selectBasCarrierKeywordList(basCarrierKeyword);
        ExcelUtil<BasCarrierKeyword> util = new ExcelUtil<BasCarrierKeyword>(BasCarrierKeyword.class);
        util.exportExcel(response, list, "BasCarrierKeyword");

    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basCarrierKeywordService.selectBasCarrierKeywordById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块", notes = "新增模块")
    public R add(@RequestBody @Valid BasCarrierKeyword basCarrierKeyword) {
        return toOk(basCarrierKeywordService.insertBasCarrierKeyword(basCarrierKeyword));
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块", notes = "修改模块")
    public R edit(@RequestBody @Valid BasCarrierKeyword basCarrierKeyword) {
        return toOk(basCarrierKeywordService.updateBasCarrierKeyword(basCarrierKeyword));
    }

    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:changeStatus')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @GetMapping("changeStatus")
    @ApiOperation(value = "变更状态", notes = "变更状态, status- 0停用 1启用")
    public R changeStatus(@RequestParam Integer id, @RequestParam String status){
        BasCarrierKeyword keyword = basCarrierKeywordService.getById(id);
        if (keyword == null) {
            return R.failed("关键词不存在");
        }
        keyword.setStatus(status);
        basCarrierKeywordService.updateById(keyword);
        applicationContext.publishEvent(new KeywordSyncEvent(keyword.getCarrierCode()));
        return R.ok();
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('BasCarrierKeyword:BasCarrierKeyword:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basCarrierKeywordService.deleteBasCarrierKeywordByIds(ids));
    }

    @GetMapping("checkExistKeyword")
    @ApiOperation(value = "检查关键词是否存在", notes = "检查关键词是否存在")
    public R checkExistKeyword(@RequestParam String carrierCode, @RequestParam String text) {
        return R.ok(basCarrierKeywordService.checkExistKeyword(carrierCode, text));
    }
}
