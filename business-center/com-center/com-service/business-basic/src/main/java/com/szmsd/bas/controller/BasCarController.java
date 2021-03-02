package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasCar;
import com.szmsd.bas.service.IBasCarService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-09-24
 */

@Api(tags = {"车辆管理"})
@RestController
@RequestMapping("/bas-car")
public class BasCarController extends BaseController {


    @Resource
    private IBasCarService basCarService;

    /**
     * 查询模块列表
     */
    @ApiOperation(value = "查询车辆", notes = "查询车辆('bas:bascar:list')")
    @PreAuthorize("@ss.hasPermi('bas:bascar:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasCar basCar) {
        startPage();
        List<BasCar> list = basCarService.selectBasCarList(basCar);
        return getDataTable(list);
    }

    /**
     * 查询模块列表
     */
    @ApiOperation(value = "查询车辆无分页APP", notes = "查询车辆('bas:bascar:list')")
    @PreAuthorize("@ss.hasPermi('bas:bascar:list')")
    @GetMapping("/lists")
    public TableDataInfo lists(BasCar basCar) {
        List<BasCar> list = basCarService.selectBasCarList(basCar);
        return getDataTable(list);
    }
    
    /**
     * 新增模块
     */
    @ApiOperation(value = "新增车辆", notes = "新增车辆('bas:bascar:add')")
    @PreAuthorize("@ss.hasPermi('bas:bascar:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasCar basCar) {
        basCar.setCreateTime(new Date());
        return toOk(basCarService.insertBasCar(basCar));
    }

    /**
     * 修改模块
     */
    @ApiOperation(value = "修改车辆", notes = "修改车辆('bas:bascar:edit')")
    @PreAuthorize("@ss.hasPermi('bas:bascar:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasCar basCar) {
        basCar.setUpdateTime(new Date());
        return toOk(basCarService.updateBasCar(basCar));
    }

    /**
     * 删除模块
     */
    @ApiOperation(value = "删除车辆", notes = "删除车辆('bas:bascar:remove')")
    @PreAuthorize("@ss.hasPermi('bas:bascar:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basCarService.deleteBasCarByIds(ids));
    }

}
