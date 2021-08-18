package com.szmsd.doc.controller;

import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.bean.request.BaseProductQueryRequest;
import com.szmsd.doc.bean.request.ProductRequest;
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
    @ApiOperation(value = "查询列表", notes = "SKU列表 - 分页查询")
    public R<TableDataInfo> list(@RequestBody BaseProductQueryRequest baseProductQueryRequest){
        return R.ok(baseProductClientService.list(BeanMapperUtil.map(baseProductQueryRequest, BaseProductQueryDto.class)));
    }

    @PreAuthorize("hasAuthority('read')")
    @PostMapping("save")
    @ApiOperation(value = "新增", notes = "SKU新增")
    public R save(@RequestBody @Validated ProductRequest productRequest){
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        baseProductClientService.add(product);
        return R.ok();
    }

    @PreAuthorize("hasAuthority('read')")
    @GetMapping("getBarCode")
    @ApiOperation(value = "SKU标签生成",notes = "生成sku标签条形码，返回的为条形码图片的Base64")
    public R getBarCode(@ApiParam("sku的code") @RequestParam String skuCode){
        Boolean valid = baseProductClientService.checkSkuValidToDelivery(skuCode);
        return valid ? R.ok(GoogleBarCodeUtils.generateBarCodeBase64(skuCode)) : R.failed("sku不存在");
    }

}
