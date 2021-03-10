package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.http.domain.HtpTransaction;
import com.szmsd.http.service.IHtpTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * <p>
 * http事务处理表 前端控制器
 * </p>
 *
 * @author asd
 * @since 2021-03-10
 */
@Api(tags = {"事务处理"})
@ApiSort(800000)
@RestController
@RequestMapping("/htp-transaction")
public class HtpTransactionController extends BaseController {

    @Resource
    private IHtpTransactionService htpTransactionService;

    @PreAuthorize("@ss.hasPermi('HtpTransaction:HtpTransaction:add')")
    @Log(title = "http事务处理表模块", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    @ApiOperation(value = "事务处理 - 创建事务", position = 100)
    @ApiImplicitParam(name = "dto", value = "HtpTransaction", dataType = "HtpTransaction")
    public R<Integer> create(@RequestBody HtpTransaction htpTransaction) {
        return toOk(htpTransactionService.insertHtpTransaction(htpTransaction));
    }
}
