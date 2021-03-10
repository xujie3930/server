package com.szmsd.open.controller;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.open.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"BaseInfo"})
@RestController
@RequestMapping("/api/base")
public class BaseInfoController extends BaseController {

    @Resource
    private BasWarehouseClientService basWarehouseClientService;

    @PostMapping("/warehouse")
    @ApiOperation(value = "#A1 创建/更新仓库")
    public ResponseVO add(@RequestBody @Validated AddWarehouseRequest addWarehouseRequest) {
        basWarehouseClientService.saveOrUpdate(addWarehouseRequest);
        return ResponseVO.ok();
    }

}
