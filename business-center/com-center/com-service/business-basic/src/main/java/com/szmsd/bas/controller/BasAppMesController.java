package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasAppMes;
import com.szmsd.bas.service.IBasAppMesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * App消息表 前端控制器
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */

@Api(tags = {"App消息表"})
@RestController
@RequestMapping("/bas-app-mes")
public class BasAppMesController extends BaseController {


    @Resource
    private IBasAppMesService basAppMesService;

    /**
     * 查询App消息表模块列表
     *
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:list')")
    @GetMapping("/list")
    public TableDataInfo list(BasAppMes basAppMes) {
        startPage();
        List<BasAppMes> list = basAppMesService.selectBasAppMesList(basAppMes);
        return getDataTable(list);
    }

    /**
     * 新增App消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:add')")
    @Log(title = "App消息表模块", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@RequestBody BasAppMes basAppMes) {
        return toOk(basAppMesService.insertBasAppMes(basAppMes));
    }

    /**
     * 修改App消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:edit')")
    @Log(title = "App消息表模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@RequestBody BasAppMes basAppMes) {
        return toOk(basAppMesService.updateBasAppMes(basAppMes));
    }

    /**
     * 删除App消息表模块
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:remove')")
    @Log(title = "App消息表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basAppMesService.deleteBasAppMesByIds(ids));
    }

    /**
     * 查询App消息表模块列表
     *
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:getPushAppMsgList')")
    @GetMapping("/getPushAppMsgList")
    public R<List<BasAppMes>> getPushAppMsgList() {
        List<BasAppMes> list = basAppMesService.getPushAppMsgList();
        return R.ok(list);
    }

    /**
     * 查询App消息表模块列表
     *
     */
    @PreAuthorize("@ss.hasPermi('BasAppMes:BasAppMes:updateBasAppMesList')")
    @PostMapping("/updateBasAppMesList")
    public R updateBasAppMesList(@RequestBody List<BasAppMes> basAppMesList) {
        boolean res =  basAppMesService.updateBasAppMesList(basAppMesList);
        if(res){
            return  R.ok();
        }
        return R.failed();
    }

}
