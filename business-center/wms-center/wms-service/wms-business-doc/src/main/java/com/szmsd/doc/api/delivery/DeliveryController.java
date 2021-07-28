package com.szmsd.doc.api.delivery;

import com.szmsd.common.core.domain.R;
import com.szmsd.doc.api.delivery.request.DelOutboundCanceledRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DeliveryController {

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/canceled")
    @ApiOperation(value = "出库管理 - 取消", position = 700)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest")
    public R<Integer> canceled(@RequestBody @Validated DelOutboundCanceledRequest request) {
        return R.ok();
    }
}
