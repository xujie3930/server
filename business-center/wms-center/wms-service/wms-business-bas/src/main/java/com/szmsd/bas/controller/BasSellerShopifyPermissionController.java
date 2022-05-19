package com.szmsd.bas.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
    @ApiImplicitParam(name = "entity", value = "参数", dataType = "BasSellerShopifyPermission")
    public R<List<BasSellerShopifyPermission>> list(@RequestBody BasSellerShopifyPermission entity) {
        LambdaQueryWrapper<BasSellerShopifyPermission> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(entity.getSellerCode()), BasSellerShopifyPermission::getSellerCode, entity.getSellerCode());
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(entity.getShop()), BasSellerShopifyPermission::getShop, entity.getShop());
        // 查询有效的
        lambdaQueryWrapper.eq(BasSellerShopifyPermission::getState, "1");
        return R.ok(this.basSellerShopifyPermissionService.list(lambdaQueryWrapper));
    }

    @PostMapping(value = "/page")
    @ApiImplicitParam(name = "entity", value = "参数", dataType = "BasSellerShopifyPermission")
    public R<IPage<BasSellerShopifyPermission>> page(@RequestBody BasSellerShopifyPermission entity) {
        IPage<BasSellerShopifyPermission> iPage = new Page<>(entity.getPageNum(), entity.getPageSize());
        LambdaQueryWrapper<BasSellerShopifyPermission> lambdaQueryWrapper = Wrappers.lambdaQuery();
        String sellerCode = entity.getSellerCode();
        if (StringUtils.isEmpty(sellerCode)) {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            if (null != loginUser) {
                sellerCode = loginUser.getSellerCode();
            }
        }
        if (StringUtils.isEmpty(sellerCode)) {
            return R.ok(iPage);
        }
        lambdaQueryWrapper.eq(BasSellerShopifyPermission::getSellerCode, sellerCode);
        return R.ok(this.basSellerShopifyPermissionService.page(iPage, lambdaQueryWrapper));
    }
}
