package com.szmsd.chargerules.controller;

import com.msd.chargerules.domain.WarehouseOperation;
import com.msd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"仓储业务计费规则"})
@RestController
@RequestMapping("/warehouseOperation")
public class WarehouseOperationController extends BaseController {

    @Resource
    private IWarehouseOperationService warehouseOperationService;

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:add')")
    @ApiOperation(value = "仓储业务计费规则 - 保存")
    @PostMapping("/save")
    public R save(@RequestBody WarehouseOperationDTO dto){
        return toOk(warehouseOperationService.save(dto));
    }

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:edit')")
    @ApiOperation(value = "仓储业务计费规则 - 修改")
    @PutMapping("/update")
    public R update(@RequestBody WarehouseOperation dto) {
        return toOk(warehouseOperationService.update(dto));
    }

    @PreAuthorize("@ss.hasPermi('WarehouseOperation:WarehouseOperation:list')")
    @ApiOperation(value = "仓储业务计费规则 - 分页查询")
    @GetMapping("/list")
    public TableDataInfo<WarehouseOperation> listPage(WarehouseOperationDTO dto){
        startPage();
        List<WarehouseOperation> list = warehouseOperationService.listPage(dto);
        return getDataTable(list);
    }

}
