package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.open.domain.OpnRequestLog;
import com.szmsd.open.service.IOpnRequestLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
@RequestMapping("/opn-request-log")
public class OpnRequestLogController extends BaseController {

    @Resource
    private IOpnRequestLogService opnRequestLogService;

    /**
     * 查询http请求日志模块列表
     */
    @PreAuthorize("@ss.hasPermi('OpnRequestLog:OpnRequestLog:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询http请求日志模块列表", notes = "查询http请求日志模块列表")
    public TableDataInfo list(OpnRequestLog opnRequestLog) {
        startPage();
        List<OpnRequestLog> list = opnRequestLogService.selectOpnRequestLogList(opnRequestLog);
        return getDataTable(list);
    }

    /**
     * 获取http请求日志模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('OpnRequestLog:OpnRequestLog:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取http请求日志模块详细信息", notes = "获取http请求日志模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(opnRequestLogService.selectOpnRequestLogById(id));
    }

}
