package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasLocation;
import com.szmsd.bas.service.IBasLocationService;
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
 * @since 2020-09-18
 */

@Api(tags = {"库位表"})
@RestController
@RequestMapping("/bas-location")
public class BasLocationController extends BaseController {


    @Resource
    private IBasLocationService basLocationService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:BasLocation:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasLocation basLocation) {
        startPage();
        List<BasLocation> list = basLocationService.selectBasLocationList(basLocation);
        return getDataTable(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('bas:baslocation:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, BasLocation basLocation) throws IOException {
        List<BasLocation> list = basLocationService.selectBasLocationList(basLocation);
        ExcelUtil<BasLocation> util = new ExcelUtil<BasLocation>(BasLocation.class);
        util.exportExcel(response, list, "BasLocation");
    }


    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('bas:baslocation:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasLocation basLocation) {
        BasLocation basLocations =new BasLocation();
        basLocations.setWarehouseCode(basLocation.getWarehouseCode());
        List<BasLocation> list = basLocationService.selectBasLocationList(basLocations);
        if (list.size()!=0){
            return R.failed("库位编号重复");
        }
        basLocation.setCreateTime(new Date());
        return toOk(basLocationService.insertBasLocation(basLocation));
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('bas:baslocation:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasLocation basLocation) {


        basLocation.setUpdateTime(new Date());
        return toOk(basLocationService.updateBasLocation(basLocation));
    }
}
