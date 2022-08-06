package com.szmsd.delivery.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.delivery.service.IDelProductTimeService;
import com.szmsd.delivery.domain.DelProductTime;
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
    *  前端控制器
    * </p>
*
* @author admin
* @since 2022-08-06
*/


@Api(tags = {"产品时效"})
@RestController
@RequestMapping("/del-product-time")
public class DelProductTimeController extends BaseController{

     @Resource
     private IDelProductTimeService delProductTimeService;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(DelProductTime delProductTime)
     {
            startPage();
            List<DelProductTime> list = delProductTimeService.selectDelProductTimeList(delProductTime);
            return getDataTable(list);
      }

    /**
    * 导出模块列表
    */
   /*  @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, DelProductTime delProductTime) throws IOException {
     List<DelProductTime> list = delProductTimeService.selectDelProductTimeList(delProductTime);
     ExcelUtil<DelProductTime> util = new ExcelUtil<DelProductTime>(DelProductTime.class);
        util.exportExcel(response,list, "DelProductTime");

     }
*/
    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return R.ok(delProductTimeService.selectDelProductTimeById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody DelProductTime delProductTime)
    {
    return toOk(delProductTimeService.insertDelProductTime(delProductTime));
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody DelProductTime delProductTime)
    {
    return toOk(delProductTimeService.updateDelProductTime(delProductTime));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('DelProductTime:DelProductTime:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<String> ids)
    {
    return toOk(delProductTimeService.deleteDelProductTimeByIds(ids));
    }

}
