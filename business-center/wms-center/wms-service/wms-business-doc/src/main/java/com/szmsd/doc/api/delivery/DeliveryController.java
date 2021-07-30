package com.szmsd.doc.api.delivery;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.api.service.DelOutboundClientService;
import com.szmsd.delivery.dto.DelOutboundCanceledDto;
import com.szmsd.doc.api.delivery.request.DelOutboundCanceledRequest;
import com.szmsd.doc.api.delivery.request.PricedProductRequest;
import com.szmsd.doc.api.delivery.response.PricedProductResponse;
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
 * @author zhangyuyuan
 * @date 2021-07-28 16:05
 */
@Api(tags = {"出库管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/outbound")
public class DeliveryController {

    @Autowired
    private DelOutboundClientService delOutboundClientService;

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/priced-product")
    @ApiOperation(value = "#1 出库管理 - 物流服务列表", position = 100)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "PricedProductRequest", required = true)
    public R<List<PricedProductResponse>> pricedProduct(@RequestBody @Validated PricedProductRequest request) {
        return R.ok();
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("/canceled")
    @ApiOperation(value = "出库管理 - 取消", position = 700)
    @ApiImplicitParam(name = "request", value = "请求参数", dataType = "DelOutboundCanceledRequest")
    public R<Integer> canceled(@RequestBody @Validated DelOutboundCanceledRequest request) {
        DelOutboundCanceledDto dto = BeanMapperUtil.map(request, DelOutboundCanceledDto.class);
        return R.ok(this.delOutboundClientService.canceled(dto));
    }
}
