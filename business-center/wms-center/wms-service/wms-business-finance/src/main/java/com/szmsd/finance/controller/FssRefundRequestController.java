package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.finance.dto.*;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.enums.ReviewStatusEnum;
import com.szmsd.finance.service.IRefundRequestService;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.finance.vo.RefundRequestVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 退费记录表 前端控制器
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */


@Api(tags = {"退费记录表"})
@RestController
@RequestMapping("/refundRequest")
public class FssRefundRequestController extends BaseController {

    @Resource
    private IRefundRequestService fssRefundRequestService;

    /**
     * 查询退费记录表模块列表
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:list')")
    @GetMapping("/page")
    @ApiOperation(value = "列表-分页", notes = "查询退费记录表模块列表")
    public TableDataInfo<RefundRequestListVO> page(@Validated RefundRequestQueryDTO queryDTO) {
        startPage();
        List<RefundRequestListVO> list = fssRefundRequestService.selectRequestList(queryDTO);
        return getDataTable(list);
    }

    /**
     * 导出退费记录表模块列表
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:export')")
    @Log(title = "退费记录表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出", notes = "导出退费记录表模块列表")
    public void export(HttpServletResponse response, @Validated RefundRequestQueryDTO queryDTO) throws IOException {
        List<RefundRequestListVO> list = fssRefundRequestService.selectRequestList(queryDTO);
        ExcelUtil<RefundRequestListVO> util = new ExcelUtil<RefundRequestListVO>(RefundRequestListVO.class);
        util.exportExcel(response, list, "退费记录-" + LocalDate.now());
    }

    List<RefundRequestDTO> list;

    {
        list = new ArrayList<>(1);
        RefundRequestDTO refundRequestDTO = new RefundRequestDTO();
        refundRequestDTO.setAmount(new BigDecimal("12.1"));
        list.add(refundRequestDTO);
    }

    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:export')")
    @Log(title = "退费记录表模块", businessType = BusinessType.EXPORT)
    @GetMapping("/exportTemplate")
    @ApiOperation(value = "导出-导入模板", notes = "导出模板")
    public void exportTemplate(HttpServletResponse response) {
        ExcelUtil<RefundRequestDTO> util = new ExcelUtil<>(RefundRequestDTO.class);
        util.exportExcel(response, list, "退费申请模板-" + LocalDate.now());
    }

    /**
     * 新增退费记录表模块
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:add')")
    @Log(title = "退费记录表模块", businessType = BusinessType.INSERT)
    @PostMapping("import/byTemplateFile")
    @ApiOperation(value = "导入", notes = "导入退费记录表模块")
    public R importByTemplate(MultipartFile file) {
        return toOk(fssRefundRequestService.importByTemplate(file));
    }

    /**
     * 获取退费记录表模块详细信息
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取退费记录表模块详细信息", notes = "获取退费记录表模块详细信息")
    public R<RefundRequestVO> getInfo(@PathVariable("id") String id) {
        return R.ok(fssRefundRequestService.selectDetailInfoById(id));
    }

    /**
     * 新增退费记录表模块
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:add')")
    @Log(title = "退费记录表模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增", notes = "新增退费记录表模块")
    public R add(@Validated @RequestBody RefundRequestListDTO addDTO) {
        return toOk(fssRefundRequestService.insertRefundRequest(addDTO));
    }

    /**
     * 修改退费记录表模块
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:edit')")
    @Log(title = "退费记录表模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = "修改", notes = "修改退费记录表模块")
    public R edit(@Validated @RequestBody RefundRequestDTO updateDTO) {
        return toOk(fssRefundRequestService.updateRefundRequest(updateDTO));
    }

    /**
     * 删除退费记录表模块
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:remove')")
    @Log(title = "退费记录表模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除", notes = "删除退费记录表模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(fssRefundRequestService.deleteRefundRequestByIds(ids));
    }

    /**
     * 审核退费记录
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:update')")
    @Log(title = "退费记录表模块", businessType = BusinessType.UPDATE)
    @GetMapping("approve/{status}/{idList}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status"),
            @ApiImplicitParam(name = "idList", value = "审核勾选列表", example = "1")
    })
    @ApiOperation(value = "审批", notes = "审核退费记录")
    public R approve(@PathVariable("status") RefundStatusEnum status, @PathVariable("idList") List<String> ids) {
        return toOk(fssRefundRequestService.approve(status, ids));
    }

    /**
     * 审核退费记录
     */
    @PreAuthorize("@ss.hasPermi('FssRefundRequest:FssRefundRequest:update')")
    @Log(title = "退费记录表模块", businessType = BusinessType.UPDATE)
    @GetMapping("confirmOperation")
    @ApiOperation(value = "系统确认-TODO", notes = "系统确认操作费确认退费")
    public R confirmOperation(@Validated @RequestBody ConfirmOperationDTO confirmOperationDTO) {
        return toOk(fssRefundRequestService.confirmOperation(confirmOperationDTO));
    }

}
