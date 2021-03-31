package com.szmsd.open.controller;

import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.inventory.api.service.IInventoryCheckClientService;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import com.szmsd.open.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = {"库存盘点"})
@RestController("/api/inventory")
public class InventoryCheckController {

    @Resource
    private IInventoryCheckClientService iInventoryCheckClientService;

    @PostMapping("/adjust")
    @ApiOperation(value = "#C1 接收仓库盘盈盘亏（盘点也是用此接口）")
    @ApiImplicitParam(name = "dto", value = "ShipmentRequestDto", dataType = "ShipmentRequestDto")
    public ResponseVO adjust(@RequestBody @Validated AdjustRequestDto adjustRequestDto) {
        this.iInventoryCheckClientService.adjust(adjustRequestDto);
        return ResponseVO.ok();
    }

    @PostMapping("/counting")
    @ApiOperation(value = "#C2 接收申请盘点结果(此接口只是记录结果，不调整库存)")
    @ApiImplicitParam(name = "dto", value = "CountingRequestDto", dataType = "CountingRequestDto")
    public ResponseVO counting(@RequestBody @Validated CountingRequestDto countingRequestDto) {
        iInventoryCheckClientService.counting(countingRequestDto);
        return ResponseVO.ok();
    }

}
