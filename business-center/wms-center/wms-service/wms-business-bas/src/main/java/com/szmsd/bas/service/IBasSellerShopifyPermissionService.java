package com.szmsd.bas.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasSellerShopifyPermission;

/**
 * <p>
 * 客户shopify授权信息 服务类
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
public interface IBasSellerShopifyPermissionService extends IService<BasSellerShopifyPermission> {

    void getAccessToken(JSONObject jsonObject);

    /**
     * 禁用shop
     *
     * @param shop shop
     */
    void disabledByShop(String shop);
}