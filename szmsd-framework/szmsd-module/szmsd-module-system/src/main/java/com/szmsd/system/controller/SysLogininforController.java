package com.szmsd.system.controller;

import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.system.domain.SysLogininfor;
import com.szmsd.system.service.ISysLogininforService;

/**
 * 系统访问记录
 *
 * @author lzw
 */
@RestController
@RequestMapping("/logininfor")
@Api(tags = "系统访问记录")
public class SysLogininforController extends BaseController {
    @Resource
    private ISysLogininforService logininforService;

    @PreAuthorize("@ss.hasPermi('system:logininfor:list')")
    @GetMapping("/list")
    @ApiOperation(httpMethod = "GET", value = "获取登陆日志列表")
    public TableDataInfo list(SysLogininfor logininfor) {
        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    @Log(title = "登陆日志", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:logininfor:export')")
    @GetMapping("/export")
    @ApiOperation(httpMethod = "POST", value = "导出登陆日志")
    public void export(HttpServletResponse response, SysLogininfor logininfor) throws IOException {
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登陆日志");
    }

    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @Log(title = "登陆日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    @ApiOperation(httpMethod = "DELETE", value = "删除登陆日志")
    public R remove(@PathVariable Long[] infoIds) {
        return toOk(logininforService.deleteLogininforByIds(infoIds));
    }

    @PreAuthorize("@ss.hasPermi('system:logininfor:remove')")
    @Log(title = "登陆日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    @ApiOperation(httpMethod = "DELETE", value = "清空登陆日志")
    public R clean() {
        logininforService.cleanLogininfor();
        return R.ok();
    }
}
