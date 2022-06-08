package com.szmsd.delivery.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelQuerySettingsService;
import com.szmsd.delivery.domain.DelQuerySettings;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
    * 查件设置 前端控制器
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/


@Api(tags = {"查件设置"})
@RestController
@RequestMapping("/del-query-settings")
public class DelQuerySettingsController extends BaseController{

     @Resource
     private IDelQuerySettingsService delQuerySettingsService;
     /**
       * 查询查件设置模块列表
     */
      @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询查件设置模块列表",notes = "查询查件设置模块列表")
      public TableDataInfo list(DelQuerySettings delQuerySettings)
     {
            startPage();
            List<DelQuerySettings> list = delQuerySettingsService.selectDelQuerySettingsList(delQuerySettings);
            return getDataTable(list);
      }

    /**
    * 导出查件设置模块列表
    */
   /*  @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:export')")
     @Log(title = "查件设置模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出查件设置模块列表",notes = "导出查件设置模块列表")
     public void export(HttpServletResponse response, DelQuerySettings delQuerySettings) throws IOException {
     List<DelQuerySettings> list = delQuerySettingsService.selectDelQuerySettingsList(delQuerySettings);
     ExcelUtil<DelQuerySettings> util = new ExcelUtil<DelQuerySettings>(DelQuerySettings.class);
        util.exportExcel(response,list, "DelQuerySettings");

     }*/

    /**
    * 获取查件设置模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取查件设置模块详细信息",notes = "获取查件设置模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(delQuerySettingsService.selectDelQuerySettingsById(id));
    }

    /**
    * 新增查件设置模块
    */
    @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:add')")
    @Log(title = "查件设置模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增查件设置模块",notes = "新增查件设置模块")
    public R add(@RequestBody DelQuerySettings delQuerySettings)
    {
    return toOk(delQuerySettingsService.insertDelQuerySettings(delQuerySettings));
    }

    /**
    * 修改查件设置模块
    */
    @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:edit')")
    @Log(title = "查件设置模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改查件设置模块",notes = "修改查件设置模块")
    public R edit(@RequestBody DelQuerySettings delQuerySettings)
    {
    return toOk(delQuerySettingsService.updateDelQuerySettings(delQuerySettings));
    }

    /**
    * 删除查件设置模块
    */
    @PreAuthorize("@ss.hasPermi('DelQuerySettings:DelQuerySettings:remove')")
    @Log(title = "查件设置模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除查件设置模块",notes = "删除查件设置模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(delQuerySettingsService.deleteDelQuerySettingsByIds(ids));
    }

}
