package com.szmsd.inventory.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import com.szmsd.inventory.service.IInventoryCheckOpenService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"库存盘点 - open"})
@RestController
@RequestMapping("/api/inventory")
public class InventoryCheckOpenController extends BaseController {

    @Resource
    private IInventoryCheckOpenService iInventoryCheckOpenService;

    @Log(title = "库存盘点模块", businessType = BusinessType.UPDATE)
    @PostMapping("/adjust")
    @ApiOperation(value = "#C1 接收仓库盘盈盘亏（盘点也是用此接口）")
    @ApiImplicitParam(name = "dto", value = "ShipmentRequestDto", dataType = "ShipmentRequestDto")
    public R<Integer> adjust(@RequestBody @Validated AdjustRequestDto adjustRequestDto) {
        return R.ok(this.iInventoryCheckOpenService.adjust(adjustRequestDto));
    }

    @Log(title = "库存盘点模块", businessType = BusinessType.INSERT)
    @PostMapping("/counting")
    @ApiOperation(value = "#C2 接收申请盘点结果(此接口只是记录结果，不调整库存)")
    @ApiImplicitParam(name = "dto", value = "CountingRequestDto", dataType = "CountingRequestDto")
    public R<Integer> counting(@RequestBody @Validated CountingRequestDto countingRequestDto) {
        return R.ok(this.iInventoryCheckOpenService.counting(countingRequestDto));
    }


}
