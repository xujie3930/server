package com.szmsd.open.controller;

import com.szmsd.bas.dto.MeasuringProductRequest;
import com.szmsd.common.core.domain.R;
import com.szmsd.exception.api.service.ExceptionInfoClientService;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import com.szmsd.open.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"异常"})
@RestController
@RequestMapping("/api/exception")
public class ExceptionController extends BaseController{

    @Resource
    private ExceptionInfoClientService exceptionInfoClientService;
    @PostMapping("/new")
    @ApiOperation(value = "#F1 接收仓库创建的异常单")
    public ResponseVO measuringProduct(@RequestBody @Validated NewExceptionRequest newExceptionRequest) {
        R.getDataAndException(exceptionInfoClientService.newException(newExceptionRequest));
        return ResponseVO.ok();
    }

    @PostMapping("/processing")
    @ApiOperation(value = "#F2 接收仓库异常单的处理")
    public ResponseVO measuringProduct(@RequestBody @Validated ProcessExceptionRequest processExceptionRequest) {
        R.getDataAndException(exceptionInfoClientService.processException(processExceptionRequest));
        return ResponseVO.ok();
    }
}
