package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.service.IHtpRequestLogService;
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
 * http请求日志 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-09
 */


@Api(tags = {"http请求日志"})
@RestController
@RequestMapping("/htp-request-log")
public class HtpRequestLogController extends BaseController {

    @Resource
    private IHtpRequestLogService htpRequestLogService;

    /**
     * 查询http请求日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询http请求日志模块列表", notes = "查询http请求日志模块列表")
    public TableDataInfo list(HtpRequestLog htpRequestLog) {
        startPage();
        List<HtpRequestLog> list = htpRequestLogService.selectHtpRequestLogList(htpRequestLog);
        return getDataTable(list);
    }

    /**
     * 导出http请求日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:export')")
    @Log(title = "http请求日志模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出http请求日志模块列表", notes = "导出http请求日志模块列表")
    public void export(HttpServletResponse response, HtpRequestLog htpRequestLog) throws IOException {
        List<HtpRequestLog> list = htpRequestLogService.selectHtpRequestLogList(htpRequestLog);
        ExcelUtil<HtpRequestLog> util = new ExcelUtil<HtpRequestLog>(HtpRequestLog.class);
        util.exportExcel(response, list, "HtpRequestLog");

    }

    /**
     * 获取http请求日志模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取http请求日志模块详细信息", notes = "获取http请求日志模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(htpRequestLogService.selectHtpRequestLogById(id));
    }

    /**
     * 新增http请求日志模块
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:add')")
    @Log(title = "http请求日志模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增http请求日志模块", notes = "新增http请求日志模块")
    public R add(@RequestBody HtpRequestLog htpRequestLog) {
        return toOk(htpRequestLogService.insertHtpRequestLog(htpRequestLog));
    }

    /**
     * 修改http请求日志模块
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:edit')")
    @Log(title = "http请求日志模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改http请求日志模块", notes = "修改http请求日志模块")
    public R edit(@RequestBody HtpRequestLog htpRequestLog) {
        return toOk(htpRequestLogService.updateHtpRequestLog(htpRequestLog));
    }

    /**
     * 删除http请求日志模块
     */
    @PreAuthorize("@ss.hasPermi('HtpRequestLog:HtpRequestLog:remove')")
    @Log(title = "http请求日志模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除http请求日志模块", notes = "删除http请求日志模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(htpRequestLogService.deleteHtpRequestLogByIds(ids));
    }

}
