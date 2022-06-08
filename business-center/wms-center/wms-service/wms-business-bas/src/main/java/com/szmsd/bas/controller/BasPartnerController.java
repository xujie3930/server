package com.szmsd.bas.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szmsd.bas.domain.BasPartner;
import com.szmsd.bas.service.IBasPartnerService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"第三方伙伴配置"})
@RestController
@RequestMapping("/bas/partner")
public class BasPartnerController extends BaseController {

    @Autowired
    private IBasPartnerService partnerService;

    @PostMapping(value = "/page")
    @ApiOperation(value = "分页查询模块列表", notes = "分页查询模块列表")
    public R<IPage<BasPartner>> page(@RequestBody BasPartner entity) {
        IPage<BasPartner> iPage = new Page<>(entity.getPageNum(), entity.getPageSize());
        LambdaQueryWrapper<BasPartner> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(StringUtils.isNotEmpty(entity.getPartnerCode()), BasPartner::getPartnerCode, entity.getPartnerCode());
        queryWrapper.like(StringUtils.isNotEmpty(entity.getPartnerName()), BasPartner::getPartnerName, entity.getPartnerName());
        return R.ok(partnerService.page(iPage, queryWrapper));
    }

    @PostMapping
    @ApiOperation(value = "保存")
    public R<Boolean> save(@RequestBody BasPartner entity) {
        return R.ok(partnerService.save(entity));
    }

    @PutMapping
    @ApiOperation(value = "修改")
    public R<Boolean> update(@RequestBody BasPartner entity) {
        return R.ok(partnerService.update(entity));
    }

    @DeleteMapping
    @ApiOperation(value = "删除")
    public R<Boolean> delete(@RequestBody BasPartner entity) {
        return R.ok(partnerService.delete(entity));
    }

    @PostMapping(value = "/getByCode")
    @ApiOperation(value = "根据编码查询")
    public R<BasPartner> getByCode(@RequestBody BasPartner entity) {
        return R.ok(partnerService.getByCode(entity));
    }
}
