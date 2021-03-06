package com.szmsd.bas.controller;

import com.szmsd.bas.domain.BasTransaction;
import com.szmsd.bas.dto.BasTransactionDTO;
import com.szmsd.bas.service.IBasTransactionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"接口版本"})
@RestController
@RequestMapping("/bas/transaction")
public class BasTransactionController extends BaseController {

    @Resource
    private IBasTransactionService transactionService;

    @PreAuthorize("@ss.hasPermi('bas:transaction:save')")
    @PostMapping("/save")
    @ApiOperation(value = "保存接口业务主键", notes = "保存接口业务主键")
    public R save(BasTransactionDTO basTransactionDTO) {
        transactionService.save(new BasTransaction().setApiCode(basTransactionDTO.getApiCode()).setTransactionId(basTransactionDTO.getTransactionId()));
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:transaction:save')")
    @PostMapping("/idempotent")
    @ApiOperation(value = "是否幂等", notes = "判断接口业务主键是否存在")
    public R<Boolean> idempotent(BasTransactionDTO basTransactionDTO) {
        Boolean result = transactionService.idempotent(basTransactionDTO.getApiCode(), basTransactionDTO.getTransactionId());
        return R.ok(result);
    }

}
