package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasKeyword;
import com.szmsd.bas.service.IBasKeywordService;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */

@Api(tags = {"关键字模块"})
@RestController
@RequestMapping("/bas-keyword")
public class BasKeywordController extends BaseController {


    @Resource
    private IBasKeywordService basKeywordService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:list')")
    @ApiOperation(value = "查询关键字列表", notes = "查询关键字列表('bas:baskeyword:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasKeyword basKeyword) {
        startPage();
        List<BasKeyword> list = basKeywordService.selectBasKeywordList(basKeyword);
        return getDataTable(list);
    }


    @PostMapping("/lists")
    public R<List<BasKeyword>> lists(@RequestBody BasKeyword basKeyword) {
        List<BasKeyword> list = basKeywordService.selectBasKeywordList(basKeyword);
        return R.ok(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:export')")
    @ApiOperation(value = "导出关键字列表", notes = "导出关键字列表('bas:baskeyword:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasKeyword basKeyword) throws IOException {
        List<BasKeyword> list = basKeywordService.selectBasKeywordList(basKeyword);
        ExcelUtil<BasKeyword> util = new ExcelUtil<BasKeyword>(BasKeyword.class);
        util.exportExcel(response, list, "BasKeyword");
    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:query')")
    @ApiOperation(value = "查询关键字列表", notes = "查询关键字列表('bas:baskeyword:query')")
    @GetMapping(value = "/{id}")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basKeywordService.selectBasKeywordById(id));
    }

    /**
     * 新增修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:add')")
    @ApiOperation(value = "新增关键字列表", notes = "新增关键字列表('bas:baskeyword:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasKeyword basKeyword) {
        String uid = UUID.randomUUID().toString().substring(0, 10);
        basKeyword.setKeywordCode(uid);
        basKeyword.setCreateTime(new Date());
        basKeywordService.insertBasKeyword(basKeyword);
        return R.ok();
    }

    /**
     * 修改新增关键字列表
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:edit')")
    @Log(title = "修改新增关键字列表", businessType = BusinessType.UPDATE)
    @ApiOperation(value = "修改关键字列表", notes = "修改关键字列表")
    @PutMapping
    public R edit(@RequestBody BasKeyword basKeyword) {
        return toOk(basKeywordService.updateBasKeyword(basKeyword));
    }


    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:baskeyword:remove')")
    @ApiOperation(value = "删除关键字列表", notes = "删除关键字列表")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basKeywordService.deleteBasKeywordByIds(ids));
    }

}
