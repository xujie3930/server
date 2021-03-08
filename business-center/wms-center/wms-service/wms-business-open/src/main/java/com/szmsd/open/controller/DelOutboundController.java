package com.szmsd.open.controller;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 出库管理
 *
 * @author asd
 * @since 2021-03-05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DelOutboundController extends BaseController {

    @Resource
    private DelOutboundClientService delOutboundClientService;

    @PostMapping("/shipment")
    @ApiOperation(value = "出库管理 - 创建")
    @ApiImplicitParam(name = "dto", value = "出库单", dataType = "DelOutboundDto")
    public R<Integer> add(@RequestBody @Validated DelOutboundDto dto) {
        return R.ok(delOutboundClientService.insertDelOutbound(dto));
    }

    @PostMapping("/success")
    @ApiImplicitParam(name = "map", value = "参数", dataType = "TestDto")
    public R<Integer> success(@RequestBody TestDto map) {
        return R.ok();
    }

    @PostMapping("/error")
    public R<Integer> error() {
        if (true) {
            throw new CommonException("999", "error");
        }
        return R.ok();
    }

}
