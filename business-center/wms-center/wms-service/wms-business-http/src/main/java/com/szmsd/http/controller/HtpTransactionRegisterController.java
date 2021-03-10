package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.domain.HtpTransactionRegister;
import com.szmsd.http.service.IHtpTransactionRegisterService;
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
 * http事务注册表 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */


@Api(tags = {"http事务注册表"})
@RestController
@RequestMapping("/htp-transaction-register")
public class HtpTransactionRegisterController extends BaseController {

    @Resource
    private IHtpTransactionRegisterService htpTransactionRegisterService;

    /**
     * 查询http事务注册表模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询http事务注册表模块列表", notes = "查询http事务注册表模块列表")
    public TableDataInfo list(HtpTransactionRegister htpTransactionRegister) {
        startPage();
        List<HtpTransactionRegister> list = htpTransactionRegisterService.selectHtpTransactionRegisterList(htpTransactionRegister);
        return getDataTable(list);
    }

    /**
     * 导出http事务注册表模块列表
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:export')")
    @Log(title = "http事务注册表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出http事务注册表模块列表", notes = "导出http事务注册表模块列表")
    public void export(HttpServletResponse response, HtpTransactionRegister htpTransactionRegister) throws IOException {
        List<HtpTransactionRegister> list = htpTransactionRegisterService.selectHtpTransactionRegisterList(htpTransactionRegister);
        ExcelUtil<HtpTransactionRegister> util = new ExcelUtil<HtpTransactionRegister>(HtpTransactionRegister.class);
        util.exportExcel(response, list, "HtpTransactionRegister");

    }

    /**
     * 获取http事务注册表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取http事务注册表模块详细信息", notes = "获取http事务注册表模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(htpTransactionRegisterService.selectHtpTransactionRegisterById(id));
    }

    /**
     * 新增http事务注册表模块
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:add')")
    @Log(title = "http事务注册表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增http事务注册表模块", notes = "新增http事务注册表模块")
    public R add(@RequestBody HtpTransactionRegister htpTransactionRegister) {
        return toOk(htpTransactionRegisterService.insertHtpTransactionRegister(htpTransactionRegister));
    }

    /**
     * 修改http事务注册表模块
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:edit')")
    @Log(title = "http事务注册表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改http事务注册表模块", notes = "修改http事务注册表模块")
    public R edit(@RequestBody HtpTransactionRegister htpTransactionRegister) {
        return toOk(htpTransactionRegisterService.updateHtpTransactionRegister(htpTransactionRegister));
    }

    /**
     * 删除http事务注册表模块
     */
    @PreAuthorize("@ss.hasPermi('HtpTransactionRegister:HtpTransactionRegister:remove')")
    @Log(title = "http事务注册表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除http事务注册表模块", notes = "删除http事务注册表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(htpTransactionRegisterService.deleteHtpTransactionRegisterByIds(ids));
    }

}
