package com.szmsd.finance.controller;


import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.finance.domain.FssBankQueryVO;
import com.szmsd.finance.service.FssBankService;
import com.szmsd.finance.vo.FssBankVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"银行账号"})
@RestController
@RequestMapping("/fssbank")
@Slf4j
public class FssBankController extends BaseController {

    @Autowired
    private FssBankService fssBankService;


    @ApiOperation(value = "查询所有银行账号")
    @GetMapping("/find-all")
    public R<List<FssBankVO>> findAll(){

        return fssBankService.findAll();
    }

    @ApiModelProperty(value = "查询银行信息")
    @GetMapping("/find-bank")
    public R<List<FssBankVO>> findBankList(){

        return fssBankService.findBank();
    }

    @ApiModelProperty(value = "查询银行账号")
    @GetMapping("/find-bank-account")
    public R<List<FssBankVO>> findBankAccountList(@RequestParam("bankCode")String bankCode){

        return fssBankService.findBankAccount(bankCode,null);
    }

    @ApiModelProperty(value = "查询银行账号")
    @PostMapping("/find-bank-account")
    public R<List<FssBankVO>> findBankAccountList(@RequestBody FssBankQueryVO fssBankQueryVO){

        return fssBankService.findBankAccount(fssBankQueryVO);
    }

}
