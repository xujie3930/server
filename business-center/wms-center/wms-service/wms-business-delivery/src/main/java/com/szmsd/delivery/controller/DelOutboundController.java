package com.szmsd.delivery.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.*;
import io.swagger.annotations.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DelOutboundController extends BaseController {

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/page")
    @ApiOperation(value = "出库管理 - 分页", position = 100)
    @AutoValue
    public TableDataInfo<DelOutboundListVO> page(@RequestBody DelOutboundListQueryDto queryDto) {
        startPage();
        return getDataTable(this.delOutboundService.selectDelOutboundList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "出库管理 - 详情", position = 200)
    public R<DelOutboundVO> getInfo(@PathVariable("id") String id) {
        return R.ok(delOutboundService.selectDelOutboundById(id));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:query')")
    @GetMapping(value = "getInfoByOrderId/{orderId}")
    @ApiOperation(value = "出库管理 - 详情", position = 201)
    public R<DelOutbound> getInfoByOrderId(@PathVariable("orderId") String orderId) {
        return R.ok(delOutboundService.selectDelOutboundByOrderId(orderId));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:add')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - 创建", position = 300)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> add(@RequestBody @Validated({ValidationSaveGroup.class}) DelOutboundDto dto) {
        return R.ok(delOutboundService.insertDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:edit')")
    @Log(title = "出库单模块", businessType = BusinessType.UPDATE)
    @PutMapping("/shipment")
    @ApiOperation(value = "出库管理 - 修改", position = 400)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> edit(@RequestBody @Validated(ValidationUpdateGroup.class) DelOutboundDto dto) {
        return R.ok(delOutboundService.updateDelOutbound(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:remove')")
    @Log(title = "出库单模块", businessType = BusinessType.DELETE)
    @DeleteMapping("/shipment")
    @ApiOperation(value = "出库管理 - 删除", position = 500)
    public R<Integer> remove(@RequestBody List<String> ids) {
        return R.ok(delOutboundService.deleteDelOutboundByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:bringVerify')")
    @PostMapping("/bringVerify")
    @ApiOperation(value = "出库管理 - 提审", position = 600)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundBringVerifyDto")
    public R<Integer> bringVerify(@RequestBody @Validated DelOutboundBringVerifyDto dto) {
        return R.ok(delOutboundBringVerifyService.bringVerify(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:canceled')")
    @PostMapping("/canceled")
    @ApiOperation(value = "出库管理 - 取消", position = 700)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundCanceledDto")
    public R<Integer> canceled(@RequestBody @Validated DelOutboundCanceledDto dto) {
        return R.ok(this.delOutboundService.canceled(dto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:exportTemplate')")
    @GetMapping("/exportTemplate")
    @ApiOperation(value = "出库管理 - 新增 - SKU导入模板", position = 800)
    public void exportTemplate(HttpServletResponse response) {
        try (ExcelWriter excel = cn.hutool.poi.excel.ExcelUtil.getWriter(true);
             ServletOutputStream out = response.getOutputStream()) {
            List<String> row1 = CollUtil.newArrayList("SKU", "数量");
            List<List<String>> rows = CollUtil.newArrayList(row1, new ArrayList<>());
            excel.write(rows, true);
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + new String("出库单SKU导入".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            excel.flush(out);
            //此处记得关闭输出Servlet流
            IoUtil.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:importdetail')")
    @PostMapping("/importDetail")
    @ApiOperation(value = "出库管理 - 新增 - SKU导入", position = 900)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "warehouseCode", value = "仓库编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "sellerCode", value = "客户编码", required = true),
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<List<DelOutboundDetailVO>> importDetail(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("sellerCode") String sellerCode, HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        AssertUtil.isTrue(StringUtils.isNotEmpty(warehouseCode), "仓库编码不能为空");
        AssertUtil.isTrue(StringUtils.isNotEmpty(sellerCode), "客户编码不能为空");
        String originalFilename = file.getOriginalFilename();
        AssertUtil.notNull(originalFilename, "导入文件名称不存在");
        int lastIndexOf = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(lastIndexOf + 1);
        boolean isXlsx = "xlsx".equals(suffix);
        AssertUtil.isTrue(isXlsx, "请上传xls或xlsx文件");
        try {
            ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(file.getInputStream(), DelOutboundDetailImportDto.class, null).sheet(0);
            List<DelOutboundDetailImportDto> dtoList = excelReaderSheetBuilder.doReadSync();
            if (CollectionUtils.isEmpty(dtoList)) {
                return R.ok();
            }
            return R.ok(delOutboundService.importDetail(warehouseCode, sellerCode, dtoList));
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("文件解析异常");
        }
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:list')")
    @PostMapping("/getDelOutboundDetailsList")
    @ApiOperation(value = "出库管理 - 按条件查询出库单及详情", position = 10000)
    public R<List<DelOutboundDetailListVO>> getDelOutboundDetailsList(@RequestBody DelOutboundListQueryDto queryDto) {
        return R.ok(delOutboundService.getDelOutboundDetailsList(queryDto));
    }

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutbound:delOutboundCharge')")
    @GetMapping("/delOutboundCharge/page")
    @ApiOperation(value = "出库管理 - 按条件查询出库单及费用详情", position = 10000)
    public TableDataInfo<DelOutboundChargeListVO> getDelOutboundCharge(DelOutboundChargeQueryDto queryDto) {
        startPage();
        return getDataTable(delOutboundService.getDelOutboundCharge(queryDto));
    }

}
