package com.szmsd.finance.controller;

import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.AccountSerialBill;
import com.szmsd.finance.dto.AccountSerialBillDTO;
import com.szmsd.finance.service.IAccountSerialBillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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

}
