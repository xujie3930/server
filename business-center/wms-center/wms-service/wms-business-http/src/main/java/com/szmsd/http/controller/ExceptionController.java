package com.szmsd.http.controller;

import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.BasCodExternalDto;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.service.IExceptionService;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"基础信息"})
@RestController
@RequestMapping("/api/exception/http")
public class ExceptionController extends BaseController {
    @Autowired
    private IExceptionService iExceptionService;

    @PutMapping("/processing")
    @ApiOperation(value = "客户提供的处理方式")
    public R<ResponseVO> processing(@RequestBody ExceptionProcessRequest exceptionProcessRequest) {
        ResponseVO responseVO = iExceptionService.processing(exceptionProcessRequest);
        return R.ok(responseVO);
    }

    @PostMapping("/basCodlist")
    @ApiOperation(value = "客户提供的处理方式")
    public R<TableDataInfo<BasCodExternal>> basCodlist(@RequestBody BasCodExternalDto basCodExternalDto) {
        startPage(basCodExternalDto);
        List<BasCodExternal> list = iExceptionService.basCodlist(basCodExternalDto);
        return R.ok(getDataTable(list));

    }
}
