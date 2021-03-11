package com.szmsd.http.api.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.api.service.ITransactionHandler;
import com.szmsd.http.dto.TransactionHandlerDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 9:46
 */
@Api(tags = {"事务处理-Client"})
@ApiSort(900000)
@RestController
@RequestMapping("/htp-transaction-handler")
public class TransactionHandlerController extends BaseController {

    @PostMapping("/get")
    @ApiOperation(value = "事务处理 - 获取数据", position = 100)
    @ApiImplicitParam(name = "dto", value = "TransactionHandlerDto", dataType = "TransactionHandlerDto")
    public R<?> get(@RequestBody TransactionHandlerDto dto) {
        // 获取处理服务
        ITransactionHandler<?, ?> transactionHandler = TransactionHandlerManager.getInstance().get(dto.getInvoiceType());
        if (null == transactionHandler) {
            return R.failed("服务[" + dto.getInvoiceType() + "]未注册");
        }
        return R.ok(transactionHandler.get(dto.getInvoiceNo(), dto.getInvoiceType()));
    }

    @PostMapping("/callback")
    @ApiOperation(value = "事务处理 - 回调方法", position = 200)
    @ApiImplicitParam(name = "dto", value = "TransactionHandlerDto", dataType = "TransactionHandlerDto")
    public R<?> callback(@RequestBody TransactionHandlerDto dto) {
        // 获取处理服务
        ITransactionHandler<?, ?> transactionHandler = TransactionHandlerManager.getInstance().get(dto.getInvoiceType());
        if (null == transactionHandler) {
            return R.failed("服务[" + dto.getInvoiceType() + "]未注册");
        }
        transactionHandler.callback(null, dto.getInvoiceNo(), dto.getInvoiceType());
        return R.ok();
    }
}
