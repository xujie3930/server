package com.szmsd.bas.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 客户shopify授权信息 前端控制器
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
@Api(tags = {"客户shopify授权信息"})
@RestController
@RequestMapping("/bas-seller-shopify-permission")
public class BasSellerShopifyPermissionController extends BaseController {

    @Resource
    private IBasSellerShopifyPermissionService basSellerShopifyPermissionService;

    @PostMapping(value = "/list")
    public R<List<BasSellerShopifyPermission>> list(@RequestBody BasSellerShopifyPermission basSellerShopifyPermission) {
        LambdaQueryWrapper<BasSellerShopifyPermission> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(basSellerShopifyPermission.getSellerCode()), BasSellerShopifyPermission::getSellerCode, basSellerShopifyPermission.getSellerCode());
        return R.ok(this.basSellerShopifyPermissionService.list(lambdaQueryWrapper));
    }
}
