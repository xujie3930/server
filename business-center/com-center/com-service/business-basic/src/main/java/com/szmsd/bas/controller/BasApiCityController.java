package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasApiCity;
import com.szmsd.bas.service.IBasApiCityService;
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
    * 第三方接口 - 城市表 前端控制器
    * </p>
*
* @author admin
* @since 2021-01-20
*/


@Api(tags = {"第三方接口 - 城市表"})
@RestController
@RequestMapping("/bas-api-city")
public class BasApiCityController extends BaseController {

     @Resource
     private IBasApiCityService basApiCityService;
     /**
       * 查询第三方接口 - 城市表模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询第三方接口 - 城市表模块列表",notes = "查询第三方接口 - 城市表模块列表")
      public TableDataInfo list(BasApiCity basApiCity)
     {
            startPage();
            List<BasApiCity> list = basApiCityService.selectBasApiCityList(basApiCity);
            return getDataTable(list);
      }

    /**
    * 导出第三方接口 - 城市表模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:export')")
     @Log(title = "第三方接口 - 城市表模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出第三方接口 - 城市表模块列表",notes = "导出第三方接口 - 城市表模块列表")
     public void export(HttpServletResponse response, BasApiCity basApiCity) throws IOException {
     List<BasApiCity> list = basApiCityService.selectBasApiCityList(basApiCity);
     ExcelUtil<BasApiCity> util = new ExcelUtil<BasApiCity>(BasApiCity.class);
        util.exportExcel(response,list, "BasApiCity");

     }

    /**
    * 获取第三方接口 - 城市表模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取第三方接口 - 城市表模块详细信息",notes = "获取第三方接口 - 城市表模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(basApiCityService.selectBasApiCityById(id));
    }

    /**
    * 新增第三方接口 - 城市表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:add')")
    @Log(title = "第三方接口 - 城市表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增第三方接口 - 城市表模块",notes = "新增第三方接口 - 城市表模块")
    public R add(@RequestBody BasApiCity basApiCity)
    {
    return toOk(basApiCityService.insertBasApiCity(basApiCity));
    }

    /**
    * 修改第三方接口 - 城市表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:edit')")
    @Log(title = "第三方接口 - 城市表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改第三方接口 - 城市表模块",notes = "修改第三方接口 - 城市表模块")
    public R edit(@RequestBody BasApiCity basApiCity)
    {
    return toOk(basApiCityService.updateBasApiCity(basApiCity));
    }

    /**
    * 删除第三方接口 - 城市表模块
    */
    @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:remove')")
    @Log(title = "第三方接口 - 城市表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除第三方接口 - 城市表模块",notes = "删除第三方接口 - 城市表模块")
    public R remove(@RequestBody List<Integer> ids)
    {
    return toOk(basApiCityService.deleteBasApiCityByIds(ids));
    }

    /**
     * 获取第三方接口 - 城市表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasApiCity:BasApiCity:getBasApiCity')")
    @GetMapping(value = "getBasApiCity")
    @ApiOperation(value = "获取第三方接口 - 城市表模块详细信息（feign调用）",notes = "获取第三方接口 - 城市表模块详细信息（feign调用）")
    public R getBasApiCity(@RequestParam("provinceName") String provinceName,
                           @RequestParam("cityName") String cityName,
                           @RequestParam("townName") String townName) {
        return R.ok(basApiCityService.getBasApiCity(provinceName,cityName,townName));
    }
}
