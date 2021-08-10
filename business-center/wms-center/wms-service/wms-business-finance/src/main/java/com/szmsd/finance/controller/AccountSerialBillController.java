package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.service.IAccountSerialBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(tags = {"流水账单"})
@RestController
@RequestMapping("/serialBill")
public class AccountSerialBillController extends BaseController {

    @Resource
    private IAccountSerialBillService accountSerialBillService;

    @PreAuthorize("@ss.hasPermi('AccountSerialBill:listPage')")
    @ApiOperation(value = "流水账单 - 列表")
    @GetMapping("/listPage")
    public TableDataInfo<AccountSerialBill> listPage(AccountSerialBillDTO dto) {
        startPage();
        return getDataTable(accountSerialBillService.listPage(dto));
    }

    @PreAuthorize("@ss.hasPermi('AccountSerialBill:list')")
    @ApiOperation(value = "第三方 - 流水账单 - 列表")
    @PostMapping("/list")
    public R<TableDataInfo<AccountSerialBill>> list(@RequestBody AccountSerialBillDTO dto) {
        QueryDto page = new QueryDto();
        page.setPageNum(dto.getPageNum());
        page.setPageSize(dto.getPageSize());
        startPage(page);
        return R.ok(getDataTable(accountSerialBillService.listPage(dto)));
    }

    @PreAuthorize("@ss.hasPermi('AccountSerialBill:export')")
    @ApiOperation(value = "流水账单 - 列表导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, AccountSerialBillDTO dto) {
        List<AccountSerialBill> list = accountSerialBillService.listPage(dto);
        ExcelUtil<AccountSerialBill> util = new ExcelUtil<>(AccountSerialBill.class);
        util.exportExcel(response,list,"业务明细");
    }

}
