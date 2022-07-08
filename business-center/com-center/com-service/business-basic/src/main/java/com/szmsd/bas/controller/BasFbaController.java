package com.szmsd.bas.controller;


import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.domain.BasFba;
import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasFbaDTO;
import com.szmsd.bas.api.domain.dto.BasRegionQueryDTO;
import com.szmsd.bas.service.IBasFbaService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * fba仓库维护 前端控制器
 * </p>
 *
 * @author jun
 * @since 2022-07-08
 */
@Api(tags = {"fba仓库维护"})
@RestController
@RequestMapping("/basFbaController")
public class BasFbaController extends BaseController {
    @Autowired
    private IBasFbaService iBasFbaService;

    /**
     * 添加修改仓库维护信息
     * @param basFba
     * @return
     */
    @PostMapping(value = "/insertBasFba")
    @ApiOperation(value = "新增和修改fba仓库",notes = "新增和修改fba仓库")
    @Log(title = "新增fba仓库", businessType = BusinessType.INSERT)
    public R insertBasFba(@RequestBody BasFba basFba){
        R r=iBasFbaService.insertBasFba(basFba);
        return  r;
    }


    @PostMapping("/selectBasFba")
    @ApiOperation(value = "查询Fba仓库模块列表", notes = "查询Fba仓库模块列表")
    public TableDataInfo selectBasFba(@RequestBody BasFbaDTO basFbaDTO) {
        startPage(basFbaDTO);
        R<List<BasFba>> r = iBasFbaService.selectBasFba(basFbaDTO);
        return getDataTable(r.getData());
    }


    @Log(title = "FBA仓库模块", businessType = BusinessType.DELETE)
    @PostMapping("/deleteBasFba")
    @ApiOperation(value = "删除fba仓库", notes = "删除fba仓库")
    public R deleteBasFba(@RequestBody BasFbaDTO basFbaDTO) {
        R r = iBasFbaService.deleteBasFba(basFbaDTO.getId());
        return r;
    }

    @Log(title = "FBA仓库模块", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ApiOperation(value = "导出FBA仓库模块模块列表", notes = "导出FBA仓库模块模块列表")
    public void export(HttpServletResponse response, @RequestBody BasFbaDTO basFbaDTO) throws IOException {
        List<BasFba> list = iBasFbaService.selectBasFba(basFbaDTO).getData();
        ExcelUtil<BasFba> util = new ExcelUtil<BasFba>(BasFba.class);
        util.exportExcel(response, list, "BasFba");
    }




}
