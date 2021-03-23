package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.PreWithdraw;
import com.szmsd.finance.dto.PreRechargeAuditDTO;
import com.szmsd.finance.dto.PreWithdrawDTO;
import com.szmsd.finance.service.IPreWithdrawService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liulei
 */
@Api(tags = {"提现"})
@RestController
@RequestMapping("/preWithdraw")
public class PreWithdrawController extends BaseController {
    @Autowired
    IPreWithdrawService preWithdrawService;

    @PreAuthorize("@ss.hasPermi('PreWithdraw:listPage')")
    @ApiOperation(value = "分页查询账户提现信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(PreWithdrawDTO dto) {
        startPage();
        List<PreWithdraw> list = preWithdrawService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('PreWithdraw:save')")
    @ApiOperation(value = "预提现")
    @PostMapping("/save")
    public R save(@RequestBody PreWithdrawDTO dto){
        return preWithdrawService.save(dto);
    }

    @PreAuthorize("@ss.hasPermi('PreWithdraw:save')")
    @ApiOperation(value = "审核提现")
    @PostMapping("/audit")
    public R audit(@RequestBody PreRechargeAuditDTO dto){
        return preWithdrawService.audit(dto);
    }
}
