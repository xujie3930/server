package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.chargerules.service.IBaseInfoService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 特殊操作调接口添加
 */
@Api(tags = {"BaseInfo"})
@RestController
@RequestMapping("/api/base")
public class BaseInfoController extends BaseController {

    @Resource
    private IBaseInfoService baseInfoService;

    @PostMapping("/specialOperation")
    @ApiOperation(value = "#A3 创建/更新特殊操作")
    public R add(@RequestBody @Validated BasSpecialOperationRequestDTO basSpecialOperationRequestDTO) {
        baseInfoService.add(basSpecialOperationRequestDTO);
        return R.ok();
    }

    @GetMapping("/specialOperation/list")
    @ApiOperation(value = "特殊操作待办 - 分页查询")
    public TableDataInfo<BasSpecialOperation> list(BasSpecialOperationRequestDTO basSpecialOperationRequestDTO) {
        startPage();
        List<BasSpecialOperation> list = baseInfoService.list(basSpecialOperationRequestDTO);
        return getDataTable(list);
    }


    /**
     * 调用接口修改收费系数、审核结果
     *
     * @param basSpecialOperation basSpecialOperation
     * @return R
     */
    @PutMapping("/specialOperation/edit")
    @ApiOperation(value = "特殊操作待办 - 修改")
    public R update(@Validated @RequestBody BasSpecialOperation basSpecialOperation) {
        return baseInfoService.update(basSpecialOperation);
    }
}
