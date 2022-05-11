package com.szmsd.bas.controller;

import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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


}
