package com.szmsd.doc.api.sku;

import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.sku.request.BaseProductQueryRequest;
import com.szmsd.doc.api.sku.request.ProductRequest;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author francis
 * @date 2021-07-31
 */
@Api(tags = {"SKU管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/sku/")
public class SkuApiController {

    @Autowired
    private BaseProductClientService baseProductClientService;

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("list")
    @ApiOperation(value = "查询列表", notes = "查询SKU信息，支持分页呈现，用于入库，或者新SKU出库、集运出库")
    public TableDataInfo list(@RequestBody BaseProductQueryRequest baseProductQueryRequest){
        return baseProductClientService.list(BeanMapperUtil.map(baseProductQueryRequest, BaseProductQueryDto.class));
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("save")
    @ApiOperation(value = "新增", notes = "创建SKU，创建成功，同步推送WMS")
    public R save(@RequestBody @Validated ProductRequest productRequest){
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        baseProductClientService.add(product);
        return R.ok();
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("getBarCode")
    @ApiOperation(value = "SKU标签生成",notes = "生成sku编号，生成标签条形码，返回的为条形码图片的Base64")
    public R getBarCode(@ApiParam("sku的code") @RequestParam String skuCode){
        Boolean valid = baseProductClientService.checkSkuValidToDelivery(skuCode);
        return valid ? R.ok(GoogleBarCodeUtils.generateBarCodeBase64(skuCode)) : R.failed("sku不存在");
    }

}
