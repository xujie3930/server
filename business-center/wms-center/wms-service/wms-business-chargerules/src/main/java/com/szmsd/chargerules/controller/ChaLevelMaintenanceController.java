package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.ChaLevelMaintenanceDtoQuery;
import com.szmsd.chargerules.service.IChaLevelMaintenanceService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author admin
* @since 2022-06-22
*/


@Api(tags = {""})
@RestController
@RequestMapping("/cha-level-maintenance")
public class ChaLevelMaintenanceController extends BaseController{

     @Resource
     private IChaLevelMaintenanceService chaLevelMaintenanceService;
     
      @PreAuthorize("@ss.hasPermi('ChaLevelMaintenance:ChaLevelMaintenance:list')")
      @GetMapping("/list")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo<ChaLevelMaintenanceDto> list(ChaLevelMaintenanceDtoQuery chaLevelMaintenance)
     {
            return chaLevelMaintenanceService.selectChaLevelMaintenanceList(chaLevelMaintenance);
      }



    @PreAuthorize("@ss.hasPermi('ChaLevelMaintenance:ChaLevelMaintenance:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id)
    {
    return chaLevelMaintenanceService.selectChaLevelMaintenanceById(id);
    }


    @PreAuthorize("@ss.hasPermi('ChaLevelMaintenance:ChaLevelMaintenance:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody ChaLevelMaintenanceDto chaLevelMaintenance)
    {
    return chaLevelMaintenanceService.insertChaLevelMaintenance(chaLevelMaintenance);
    }

    @PreAuthorize("@ss.hasPermi('ChaLevelMaintenance:ChaLevelMaintenance:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody ChaLevelMaintenanceDto chaLevelMaintenance)
    {
    return chaLevelMaintenanceService.updateChaLevelMaintenance(chaLevelMaintenance);
    }


    @PreAuthorize("@ss.hasPermi('ChaLevelMaintenance:ChaLevelMaintenance:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody String id)
    {
    return chaLevelMaintenanceService.deleteChaLevelMaintenanceById(id);
    }

}
