package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理 - DOC支持"})
@ApiSort(9000)
@RestController
@RequestMapping("/api/outbound/doc")
public class DelOutboundDocController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(DelOutboundDocController.class);

    @Autowired
    private IDelOutboundDocService delOutboundDocService;
    @Autowired
    private IDelOutboundBringVerifyService iDelOutboundBringVerifyService;
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundDoc:add')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - DOC支持 - 创建", position = 100)
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<List<DelOutboundAddResponse>> add(@RequestBody List<DelOutboundDto> dto) {
        return R.ok(delOutboundDocService.add(dto));
    }
    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundDoc:add')")
    @Log(title = "出库单模块", businessType = BusinessType.INSERT)
    @PostMapping("/updateShipmentLabel")
    @ApiOperation(value = "出库管理 - DOC支持 - 修改发货指令", position = 100)
    @ApiImplicitParam(name = "idList", value = "出库单id", dataType = "String")
    public R updateShipmentLabel(@RequestBody List<String> idList) {
        iDelOutboundBringVerifyService.updateShipmentLabel(idList);
        return R.ok(1);
    }

    @Log(title = "根据仓库获取发货服务", businessType = BusinessType.OTHER)
    @PostMapping("/getShippingService")
    @ApiOperation(value = "根据仓库获取发货服务", position = 100)
    public R<List<PricedProduct>> getShippingService(DelOutboundOtherInServiceDto delOutboundOtherInServiceDto){
        return R.ok(delOutboundDocService.inService2(delOutboundOtherInServiceDto));
    }

}
