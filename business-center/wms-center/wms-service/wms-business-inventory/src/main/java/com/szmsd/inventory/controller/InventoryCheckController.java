package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;
import com.szmsd.inventory.service.IInventoryCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"库存盘点"})
@RestController
@RequestMapping("/inventory/check")
public class InventoryCheckController extends BaseController {

    @Resource
    private IInventoryCheckService iInventoryCheckService;

    @PreAuthorize("@ss.hasPermi('inventory:check:add')")
    @PostMapping("/add")
    @ApiOperation(value = "库存盘点 - 申请盘点")
    public R<Integer> add(@RequestBody InventoryCheckDTO inventoryCheckDTO) {
        return R.ok(this.iInventoryCheckService.add(inventoryCheckDTO));
    }

    @PreAuthorize("@ss.hasPermi('inventory:check:findList')")
    @GetMapping("/list")
    @ApiOperation(value = "库存盘点 - 查询列表")
    public TableDataInfo<InventoryCheck> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO) {
        startPage();
        List<InventoryCheck> list = iInventoryCheckService.findList(inventoryCheckQueryDTO);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('inventory:check:details')")
    @GetMapping("/details/{id}")
    @ApiOperation(value = "库存盘点 - 查询详情")
    public R<InventoryCheck> details(@PathVariable int id) {
        return R.ok(this.iInventoryCheckService.details(id));
    }

    @PreAuthorize("@ss.hasPermi('inventory:check:update')")
    @PutMapping("/update")
    @ApiOperation(value = "库存盘点 - 修改")
    public R<Integer> update(@RequestBody InventoryCheck inventoryCheck) {
        return R.ok(this.iInventoryCheckService.update(inventoryCheck));
    }

}
