package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"库存"})
@RestController
@RequestMapping("/inventory")
public class InventoryController extends BaseController {

    @Resource
    private IInventoryService inventoryService;

    @Resource
    private IInventoryRecordService iInventoryRecordService;

    @PreAuthorize("@ss.hasPermi('inventory:inbound')")
    @PostMapping("/inbound")
    @ApiOperation(value = "#入库上架", notes = "库存管理 - Inbound - /api/inbound/receiving #B1 接收入库上架 - 修改库存")
    public R inbound(@RequestBody InboundInventoryDTO inboundInventoryDTO) {
        inventoryService.inbound(inboundInventoryDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('inbound:receipt:page')")
    @GetMapping("/inventory/record/page")
    @ApiOperation(value = "日志记录", notes = "库存日志")
    public TableDataInfo<InventoryRecordVO> logsPage(InventoryRecordQueryDTO inventoryRecordQueryDTO) {
        startPage();
        List<InventoryRecordVO> inventoryRecordVOS = iInventoryRecordService.selectList(inventoryRecordQueryDTO);
        return getDataTable(inventoryRecordVOS);
    }

}
