package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasTemplate;
import com.szmsd.bas.service.IBasTemplateService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */

@Api(tags = {"短信模板"})
@RestController
@RequestMapping("/bas-template")
public class BasTemplateController extends BaseController {


    @Resource
    private IBasTemplateService basTemplateService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasTemplate basTemplate) {
        startPage();
        List<BasTemplate> list = basTemplateService.selectBasTemplateList(basTemplate);
        return getDataTable(list);
    }

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:list')")
    @GetMapping("/lists")
    public R lists(BasTemplate basTemplate) {
        List<BasTemplate> list = basTemplateService.selectBasTemplateList(basTemplate);
        return R.ok(list);
    }


    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasTemplate basTemplate) throws IOException {
        List<BasTemplate> list = basTemplateService.selectBasTemplateList(basTemplate);
        ExcelUtil<BasTemplate> util = new ExcelUtil<BasTemplate>(BasTemplate.class);
        util.exportExcel(response, list, "BasTemplate");
    }


    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasTemplate basTemplate) {
        basTemplate.setCreateTime(new Date());
        return toOk(basTemplateService.insertBasTemplate(basTemplate));
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasTemplate basTemplate) {
        basTemplate.setUpdateTime(new Date());
        return toOk(basTemplateService.updateBasTemplate(basTemplate));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('bas:bastemplate:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basTemplateService.deleteBasTemplateByIds(ids));
    }

}
