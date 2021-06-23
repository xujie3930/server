package com.szmsd.http.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.http.service.IInventoryService;
import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.http.vo.ResponseVO2;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = {"Inventory"})
@RestController
@RequestMapping("/api/inventory/http")
public class InventoryController extends BaseController {

    @Resource
    private IInventoryService iInventoryService;

    @GetMapping("/listing")
    @ApiOperation(value = "I2 获取库存")
    public R<List<InventoryInfo>> listing(String warehouseCode, String sku) {
        ResponseVO2<InventoryInfo> listing = iInventoryService.listing(warehouseCode, sku);
        return listing == null ? null : R.ok(listing.getRecordData());
    }
}
