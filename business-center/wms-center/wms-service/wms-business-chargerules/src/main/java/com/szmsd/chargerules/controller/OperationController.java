package com.szmsd.chargerules.controller;

import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"业务计费规则"})
@RestController
@RequestMapping("/operation")
public class OperationController extends BaseController {

    @Resource
    private IOperationService operationService;

    @PreAuthorize("@ss.hasPermi('Operation:Operation:add')")
    @ApiOperation(value = "业务计费逻辑 - 保存")
    @PostMapping("/save")
    public R save(@RequestBody OperationDTO dto){
        return toOk(operationService.save(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:edit')")
    @ApiOperation(value = "业务计费逻辑 - 修改")
    @PutMapping("/update")
    public R update(@RequestBody Operation dto) {
        return toOk(operationService.update(dto));
    }

    @PreAuthorize("@ss.hasPermi('Operation:Operation:list')")
    @ApiOperation(value = "业务计费逻辑 - 分页查询")
    @GetMapping("/list")
    public TableDataInfo<Operation> listPage(OperationDTO dto){
        startPage();
        List<Operation> list = operationService.listPage(dto);
        return getDataTable(list);
    }

}
