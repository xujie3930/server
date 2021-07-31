package com.szmsd.doc.controller;

import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.api.service.BaseProductClientService;
import com.szmsd.bas.dto.BaseProductDto;
import com.szmsd.bas.dto.BaseProductQueryDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.doc.api.delivery.RestfulResponse;
import com.szmsd.doc.bean.request.BaseProductQueryRequest;
import com.szmsd.doc.bean.request.ProductRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:05
 */
@Api(tags = {"SKU管理"})
@ApiSort(100)
@RestController
@RequestMapping("/api/sku/")
public class SkuApiController {

    @Autowired
    private BaseProductClientService baseProductClientService;

    @PostMapping("list")
    @ApiOperation(value = "查询列表", notes = "SKU列表 - 分页查询")
    public R<TableDataInfo> list(@RequestBody BaseProductQueryRequest baseProductQueryRequest){
        return R.ok(baseProductClientService.list(BeanMapperUtil.map(baseProductQueryRequest, BaseProductQueryDto.class)));
    }

    @PostMapping("save")
    @ApiOperation(value = "新增", notes = "SKU新增")
    public R save(@RequestBody @Validated ProductRequest productRequest){
        BaseProductDto product = BeanMapperUtil.map(productRequest, BaseProductDto.class);
        baseProductClientService.add(product);
        return R.ok();
    }

}
