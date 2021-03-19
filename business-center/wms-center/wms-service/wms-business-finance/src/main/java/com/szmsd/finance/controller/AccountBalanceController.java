package com.szmsd.finance.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.domain.AccountBalanceChange;
import com.szmsd.finance.dto.AccountBalanceChangeDTO;
import com.szmsd.finance.dto.AccountBalanceDTO;
import com.szmsd.finance.dto.BalanceExchangeDTO;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.service.IAccountBalanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liulei
 */
@Api(tags = {"账户余额管理"})
@RestController
@RequestMapping("/accountBalance")
public class AccountBalanceController extends FssBaseController {
    @Autowired
    IAccountBalanceService accountBalanceService;

    @PreAuthorize("@ss.hasPermi('ExchangeRate:listPage')")
    @ApiOperation(value = "分页查询账户余额信息")
    @GetMapping("/listPage")
    public TableDataInfo listPage(AccountBalanceDTO dto) {
        startPage();
        List<AccountBalance> list = accountBalanceService.listPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:recordListPage')")
    @ApiOperation(value = "分页查询账户余额变动")
    @GetMapping("/recordListPage")
    public TableDataInfo recordListPage(AccountBalanceChangeDTO dto) {
        startPage();
        List<AccountBalanceChange> list = accountBalanceService.recordListPage(dto);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:onlineIncome')")
    @ApiOperation(value = "预充值")
    @PostMapping("/preOnlineIncome")
    public R preOnlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.onlineIncome(getLoginUser(), dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:onlineIncome')")
    @ApiOperation(value = "在线充值")
    @PostMapping("/onlineIncome")
    public R onlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.onlineIncome(getLoginUser(), dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:offlineIncome')")
    @ApiOperation(value = "线下充值")
    @PostMapping("/offlineIncome")
    public R offlineIncome(@RequestBody CustPayDTO dto) {
        return accountBalanceService.offlineIncome(getLoginUser(), dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:withdraw')")
    @ApiOperation(value = "提现")
    @PostMapping("/withdraw")
    public R withdraw(@RequestBody CustPayDTO dto) {
        return accountBalanceService.withdraw(getLoginUser(), dto);
    }

    @PreAuthorize("@ss.hasPermi('ExchangeRate:balanceExchange')")
    @ApiOperation(value = "余额汇率转换")
    @PostMapping("/balanceExchange")
    public R balanceExchange(@RequestBody CustPayDTO dto) {
        return accountBalanceService.balanceExchange(getLoginUser(), dto);
    }
}