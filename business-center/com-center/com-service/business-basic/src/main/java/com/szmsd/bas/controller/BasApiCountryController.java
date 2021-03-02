package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasApiCountry;
import com.szmsd.bas.service.IBasApiCountryService;
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
    * 第三方接口 - 国家表 前端控制器
    * </p>
*
* @author admin
* @since 2021-01-20
*/


@Api(tags = {"第三方接口 - 国家表"})
@RestController
@RequestMapping("/bas-api-country")
public class BasApiCountryController extends BaseController {

     @Resource
     private IBasApiCountryService basApiCountryService;
     /**
       * 查询第三方接口 - 国家表模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询第三方接口 - 国家表模块列表",notes = "查询第三方接口 - 国家表模块列表")
      public TableDataInfo list(BasApiCountry basApiCountry)
     {
            startPage();
            List<BasApiCountry> list = basApiCountryService.selectBasApiCountryList(basApiCountry);
            return getDataTable(list);
      }

    /**
    * 导出第三方接口 - 国家表模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:export')")
     @Log(title = "第三方接口 - 国家表模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出第三方接口 - 国家表模块列表",notes = "导出第三方接口 - 国家表模块列表")
     public void export(HttpServletResponse response, BasApiCountry basApiCountry) throws IOException {
     List<BasApiCountry> list = basApiCountryService.selectBasApiCountryList(basApiCountry);
     ExcelUtil<BasApiCountry> util = new ExcelUtil<BasApiCountry>(BasApiCountry.class);
        util.exportExcel(response,list, "BasApiCountry");

     }

    /**
    * 获取第三方接口 - 国家表模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取第三方接口 - 国家表模块详细信息",notes = "获取第三方接口 - 国家表模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basApiCountryService.selectBasApiCountryById(id));
    }

    /**
    * 新增第三方接口 - 国家表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:add')")
    @Log(title = "第三方接口 - 国家表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增第三方接口 - 国家表模块",notes = "新增第三方接口 - 国家表模块")
    public R add(@RequestBody BasApiCountry basApiCountry)
    {
    return toOk(basApiCountryService.insertBasApiCountry(basApiCountry));
    }

    /**
    * 修改第三方接口 - 国家表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:edit')")
    @Log(title = "第三方接口 - 国家表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改第三方接口 - 国家表模块",notes = "修改第三方接口 - 国家表模块")
    public R edit(@RequestBody BasApiCountry basApiCountry)
    {
    return toOk(basApiCountryService.updateBasApiCountry(basApiCountry));
    }

    /**
    * 删除第三方接口 - 国家表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:remove')")
    @Log(title = "第三方接口 - 国家表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除第三方接口 - 国家表模块",notes = "删除第三方接口 - 国家表模块")
    public R remove(@RequestBody List<Integer> ids)
    {
    return toOk(basApiCountryService.deleteBasApiCountryByIds(ids));
    }

    /**
     * 获取第三方接口 - 国家表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasApiCountry:BasApiCountry:getCountryByName')")
    @GetMapping(value = "getCountryByName")
    @ApiOperation(value = "根据name获取第三方接口 - 国家表模块详细信息（feign调用）",notes = "根据name获取第三方接口 - 国家表模块详细信息（feign调用）")
    public R getCountryByName(@RequestParam("name") String name) {
        return R.ok(basApiCountryService.getCountryByName(name));
    }

}
