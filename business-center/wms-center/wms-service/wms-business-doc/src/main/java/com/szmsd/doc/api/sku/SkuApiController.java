package com.szmsd.doc.api.sku;

import com.szmsd.bas.api.feign.BasePackingFeignService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.sku.request.BaseProductQueryRequest;
import com.szmsd.doc.api.sku.request.ProductRequest;
import com.szmsd.doc.api.sku.resp.BaseProductResp;
import com.szmsd.doc.component.IRemoterApi;
import com.szmsd.doc.config.DocSubConfigData;
import com.szmsd.doc.utils.GoogleBarCodeUtils;
import com.szmsd.doc.validator.CurrentUserInfo;
import io.swagger.annotations.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
    @Resource
    private DocSubConfigData docSubConfigData;
    @Resource
    private BasePackingFeignService basePackingFeignService;
    @Resource
    private IRemoterApi remoterApi;

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("list")
    @ApiOperation(value = "查询列表", notes = "查询SKU信息，支持分页呈现，用于入库，或者新SKU出库、集运出库")
    public TableDataInfo<BaseProductResp> list(@Validated @RequestBody BaseProductQueryRequest baseProductQueryRequest) {
        baseProductQueryRequest.setSellerCode(CurrentUserInfo.getSellerCode());
        baseProductQueryRequest.setPageSize(999);
        TableDataInfo<BaseProduct> list = baseProductClientService.list(BeanMapperUtil.map(baseProductQueryRequest, BaseProductQueryDto.class));
        TableDataInfo<BaseProductResp> baseProductResp = new TableDataInfo<>();
        BeanUtils.copyProperties(list, baseProductResp);
        List<BaseProduct> rows = list.getRows();
        List<BaseProductResp> collect = rows.stream().map(x -> {
            BaseProductResp baseProductResp1 = new BaseProductResp();
            BeanUtils.copyProperties(x, baseProductResp1);
            return baseProductResp1;
        }).collect(Collectors.toList());
        baseProductResp.setRows(collect);
        return baseProductResp;
    }

    @PreAuthorize("hasAuthority('client')")
    @PostMapping("save")
    @ApiOperation(value = "新增", notes = "创建SKU，创建成功，同步推送WMS")
    public R save(@RequestBody @Validated ProductRequest productRequest) {
        productRequest.setSellerCode(CurrentUserInfo.getSellerCode());
        productRequest.validData(docSubConfigData).calculateTheVolume().checkPack(basePackingFeignService).setTheCode(remoterApi, docSubConfigData).uploadFile(remoterApi);
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        baseProductClientService.add(product);
        return R.ok();
    }

    @PreAuthorize("hasAuthority('client')")
    @GetMapping("getBarCode/{sellerCode}/{skuCode}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sellerCode", value = "用户code", required = true),
            @ApiImplicitParam(name = "skuCode", value = "sku", required = true),
    })
    @ApiOperation(value = "SKU标签生成", notes = "生成sku编号，生成标签条形码，返回的为条形码图片的Base64")
    public R getBarCode(@PathVariable("sellerCode") String sellerCode, @PathVariable("skuCode") String skuCode) {
        Boolean valid = baseProductClientService.checkSkuValidToDelivery(skuCode);
        boolean b = remoterApi.checkSkuBelong(sellerCode, null, skuCode);
        AssertUtil.isTrue(b, String.format("请检查SKU:%s是否属于用户%s", skuCode, sellerCode));
        return valid ? R.ok(GoogleBarCodeUtils.generateBarCodeBase64(skuCode)) : R.failed("sku不存在");
    }

}
