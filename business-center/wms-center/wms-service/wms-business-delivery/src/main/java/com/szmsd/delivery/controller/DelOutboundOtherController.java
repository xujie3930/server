package com.szmsd.delivery.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.dto.DelOutboundOtherInServiceDto;
import com.szmsd.delivery.service.IDelOutboundDocService;
import com.szmsd.http.vo.PricedProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
@Api(tags = {"出库管理 - 其它服务"})
@ApiSort(200)
@RestController
@RequestMapping("/api/outbound/other")
public class DelOutboundOtherController extends BaseController {

    @Autowired
    private IDelOutboundDocService delOutboundDocService;

    @PreAuthorize("@ss.hasPermi('DelOutbound:DelOutboundOther:inService')")
    @PostMapping("/inService")
    @ApiOperation(value = "出库管理 - 其它服务 - 物流服务", position = 100)
    @ApiImplicitParam(name = "dto", value = "参数", dataType = "DelOutboundOtherInServiceDto")
    public R<List<PricedProduct>> inService(@RequestBody @Validated DelOutboundOtherInServiceDto dto) {
        return R.ok(this.delOutboundDocService.inService(dto));
    }
}
