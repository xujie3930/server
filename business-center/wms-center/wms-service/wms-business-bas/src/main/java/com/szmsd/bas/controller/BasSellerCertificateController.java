package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasSellerCertificate;
import com.szmsd.bas.dto.VatQueryDto;
import com.szmsd.bas.service.IBasSellerCertificateService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
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
 * 前端控制器
 * </p>
 *
 * @author l
 * @since 2021-03-10
 */


@Api(tags = {"卖家认证信息模块"})
@RestController
@RequestMapping("/bas/sellerCertificate")
public class BasSellerCertificateController extends BaseController {

    @Resource
    private IBasSellerCertificateService basSellerCertificateService;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    public TableDataInfo list(BasSellerCertificate basSellerCertificate) {
        startPage();
        List<BasSellerCertificate> list = basSellerCertificateService.selectBasSellerCertificateList(basSellerCertificate);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:list')")
    @PostMapping("/listVAT")
    @ApiOperation(value = "查询VAT模块列表", notes = "查询VAT模块列表")
    public R<List<BasSellerCertificate>> listVAT(@RequestBody VatQueryDto vatQueryDto) {
        List<BasSellerCertificate> list = basSellerCertificateService.listVAT(vatQueryDto);
        return R.ok(list);
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, BasSellerCertificate basSellerCertificate) throws IOException {
        List<BasSellerCertificate> list = basSellerCertificateService.selectBasSellerCertificateList(basSellerCertificate);
        ExcelUtil<BasSellerCertificate> util = new ExcelUtil<BasSellerCertificate>(BasSellerCertificate.class);
        util.exportExcel(response, list, "BasSellerCertificate");

    }

    /**
     * 获取模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息", notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") String id) {
        return R.ok(basSellerCertificateService.selectBasSellerCertificateById(id));
    }

    /**
     * 新增模块
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块", notes = "新增模块")
    public R add(@RequestBody BasSellerCertificate basSellerCertificate) {
        return toOk(basSellerCertificateService.insertBasSellerCertificate(basSellerCertificate));
    }

    /**
     * 修改模块
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块", notes = "修改模块")
    public R edit(@RequestBody BasSellerCertificate basSellerCertificate) {
        return toOk(basSellerCertificateService.updateBasSellerCertificate(basSellerCertificate));
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('BasSellerCertificate:BasSellerCertificate:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(basSellerCertificateService.deleteBasSellerCertificateByIds(ids));
    }

}
