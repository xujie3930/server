package com.szmsd.bas.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.domain.BasSellerShopifyPermission;

import java.util.Map;

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
}