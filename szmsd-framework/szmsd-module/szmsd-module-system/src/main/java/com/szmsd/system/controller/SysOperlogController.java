package com.szmsd.system.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.system.api.domain.SysOperLog;
import com.szmsd.system.service.ISysOperLogService;

/**
 * 操作日志记录
 * 
 * @author lzw
 */
@RestController
@RequestMapping("/operlog")
@Api(tags = "操作日志记录")
public class SysOperlogController extends BaseController
{
    @Resource
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('system:operlog:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取操作日志列表")
    public TableDataInfo list(SysOperLog operLog)
    {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:operlog:export')")
    @GetMapping("/export")
    @ApiOperation(httpMethod = "POST", value = "导出数据")
    public void export(HttpServletResponse response, SysOperLog operLog) throws IOException
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @DeleteMapping("/{operIds}")
    @ApiOperation(httpMethod = "DELETE", value = "删除日志记录")
    public R remove(@PathVariable Long[] operIds)
    {
        return toOk(operLogService.deleteOperLogByIds(operIds));
    }

    @PreAuthorize("@ss.hasPermi('system:operlog:remove')")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    @ApiOperation(httpMethod = "DELETE", value = "清空操作日志数据")
    public R clean()
    {
        operLogService.cleanOperLog();
        return R.ok();
    }

    @PostMapping("/add")
    @ApiOperation(httpMethod = "POST", value = "新增日志")
    public R add(@RequestBody SysOperLog operLog)
    {
        return toOk(operLogService.insertOperlog(operLog));
    }
}
