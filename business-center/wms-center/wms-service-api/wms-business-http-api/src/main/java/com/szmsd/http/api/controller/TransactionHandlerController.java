package com.szmsd.http.api.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.dto.TransactionHandlerDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 9:46
 */
@Api(tags = {"事务处理"})
@ApiSort(900000)
@RestController
@RequestMapping("/htp-transaction-handler")
public class TransactionHandlerController {

    @Autowired
    private ApplicationContext applicationContext;

    @PostMapping("/get")
    @ApiOperation(value = "事务处理 - 获取数据", position = 100)
    @ApiImplicitParam(name = "dto", value = "TransactionHandlerDto", dataType = "TransactionHandlerDto")
    public R<?> get(@RequestBody TransactionHandlerDto dto) {

        return R.ok();
    }
}
