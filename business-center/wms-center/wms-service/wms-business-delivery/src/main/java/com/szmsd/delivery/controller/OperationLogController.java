package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.domain.OperationLog;
import com.szmsd.delivery.service.IOperationLogService;
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
 * 业务操作日志 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-06-21
 */


@Api(tags = {"业务操作日志"})
@RestController
@RequestMapping("/operation-log")
public class OperationLogController extends BaseController {

    @Resource
    private IOperationLogService operationLogService;

    /**
     * 查询业务操作日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询业务操作日志模块列表", notes = "查询业务操作日志模块列表")
    public TableDataInfo list(OperationLog operationLog) {
        startPage();
        List<OperationLog> list = operationLogService.selectOperationLogList(operationLog);
        return getDataTable(list);
    }

    /**
     * 导出业务操作日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:export')")
    @Log(title = "业务操作日志模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出业务操作日志模块列表", notes = "导出业务操作日志模块列表")
    public void export(HttpServletResponse response, OperationLog operationLog) throws IOException {
        List<OperationLog> list = operationLogService.selectOperationLogList(operationLog);
        ExcelUtil<OperationLog> util = new ExcelUtil<OperationLog>(OperationLog.class);
        util.exportExcel(response, list, "OperationLog");
    }

    /**
     * 获取业务操作日志模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取业务操作日志模块详细信息", notes = "获取业务操作日志模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(operationLogService.selectOperationLogById(id));
    }

    /**
     * 新增业务操作日志模块
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:add')")
    @Log(title = "业务操作日志模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增业务操作日志模块", notes = "新增业务操作日志模块")
    public R add(@RequestBody OperationLog operationLog) {
        return toOk(operationLogService.insertOperationLog(operationLog));
    }

    /**
     * 修改业务操作日志模块
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:edit')")
    @Log(title = "业务操作日志模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改业务操作日志模块", notes = "修改业务操作日志模块")
    public R edit(@RequestBody OperationLog operationLog) {
        return toOk(operationLogService.updateOperationLog(operationLog));
    }

    /**
     * 删除业务操作日志模块
     */
    @PreAuthorize("@ss.hasPermi('OperationLog:OperationLog:remove')")
    @Log(title = "业务操作日志模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除业务操作日志模块", notes = "删除业务操作日志模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(operationLogService.deleteOperationLogByIds(ids));
    }

}
