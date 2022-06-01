package com.szmsd.bas.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.config.ShopifyAppConfig;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.bas.mapper.BasSellerShopifyPermissionMapper;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

/**
 * <p>
 * 客户shopify授权信息 服务实现类
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
@Service
public class BasSellerShopifyPermissionServiceImpl extends ServiceImpl<BasSellerShopifyPermissionMapper, BasSellerShopifyPermission> implements IBasSellerShopifyPermissionService {
    private final Logger logger = LoggerFactory.getLogger(BasSellerShopifyPermissionServiceImpl.class);

    @Autowired
    private IBasSellerService sellerService;
    @Autowired
    private ShopifyAppConfig shopifyAppConfig;

    // username
    // code=dfa03ea98ce4679b4cb5b4fc67d319d0
    // &hmac=00903569aaba929eb91c57dee6f79eec4f4413a19f2d587cf11b2ac3ebf8154e
    // &host=dGVzdC1kbS1mdWxmaWxsbWVudC5teXNob3BpZnkuY29tL2FkbWlu
    // &shop=test-dm-fulfillment.myshopify.com
    // &state=20220511104154
    // &timestamp=1652237081
    // 额外参数：scope
    @Async
    @Override
    public void getAccessToken(JSONObject jsonObject) {
        String username = jsonObject.getString("username");
        String shop = jsonObject.getString("shop");
        // 查询是否要去获取永久token
        LambdaQueryWrapper<BasSellerShopifyPermission> sellerShopifyPermissionLambdaQueryWrapper = Wrappers.lambdaQuery();
        sellerShopifyPermissionLambdaQueryWrapper.eq(BasSellerShopifyPermission::getSellerName, username);
        sellerShopifyPermissionLambdaQueryWrapper.eq(BasSellerShopifyPermission::getShop, shop);
        sellerShopifyPermissionLambdaQueryWrapper.last("LIMIT 1");
        BasSellerShopifyPermission basSellerShopifyPermission = super.getOne(sellerShopifyPermissionLambdaQueryWrapper);
        logger.info("查询客户的Shopify授权信息，username: {}, shop: {}, 结果：{}", username, shop, basSellerShopifyPermission);
        boolean queryAccessToken = false;
        String accessToken = "";
        Long id = null;
        if (null == basSellerShopifyPermission) {
            // 没有授权下信息，需要去查询
            queryAccessToken = true;
        } else {
            // 状态，1有效，2无效
            String state = basSellerShopifyPermission.getState();
            if ("1".equals(state)) {
                // 获取到授权码信息
                // accessToken = basSellerShopifyPermission.getAccessToken();
                id = basSellerShopifyPermission.getId();
                // 只要登录，就重新去获取token信息
                queryAccessToken = true;
            } else {
                // 授权信息是无效的，需要去查询
                queryAccessToken = true;
            }
        }
        if (queryAccessToken) {
            // 查询客户信息
            QueryWrapper<BasSeller> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_name", username);
            queryWrapper.last("LIMIT 1");
            BasSeller seller = this.sellerService.getOne(queryWrapper);
            if (null != seller) {
                String url = "https://" + shop + "/admin/oauth/access_token";
                JSONObject bodyJsonObject = new JSONObject();
                bodyJsonObject.put("client_id", this.shopifyAppConfig.getClientId());
                bodyJsonObject.put("client_secret", this.shopifyAppConfig.getClientSecret());
                bodyJsonObject.put("code", jsonObject.getString("code"));
                String responseBody = null;
                try {
                    String body = bodyJsonObject.toJSONString();
                    responseBody = HttpUtil.post(url, body);
                    logger.info("请求shopify获取access_token，url: {}, body: {}, responseBody: {}", url, body, responseBody);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                if (StringUtils.isNotEmpty(responseBody)) {
                    JSONObject responseObject = JSONObject.parseObject(responseBody);
                    if (responseObject.containsKey("access_token")) {
                        BasSellerShopifyPermission sellerShopifyPermission = new BasSellerShopifyPermission();
                        sellerShopifyPermission.setSellerId(seller.getId());
                        sellerShopifyPermission.setSellerCode(seller.getSellerCode());
                        sellerShopifyPermission.setSellerName(seller.getUserName());
                        sellerShopifyPermission.setScope(jsonObject.getString("scope"));
                        sellerShopifyPermission.setAccessToken(accessToken = String.valueOf(responseObject.get("access_token")));
                        sellerShopifyPermission.setOauthScope(String.valueOf(responseObject.get("scope")));
                        sellerShopifyPermission.setShop(shop);
                        sellerShopifyPermission.setState("1");
                        String locations = this.getLocations(shop, accessToken);
                        sellerShopifyPermission.setLocations(locations);
                        // 如果存在，就修改，不存在就新增
                        if (null != basSellerShopifyPermission) {
                            sellerShopifyPermission.setId(basSellerShopifyPermission.getId());
                            super.updateById(sellerShopifyPermission);
                        } else {
                            super.save(sellerShopifyPermission);
                            id = sellerShopifyPermission.getId();
                        }
                    }
                }
            }
        } else {
            // 查询地址信息
            if (StringUtils.isNotEmpty(accessToken) && null != id) {
                String locations = this.getLocations(shop, accessToken);
                BasSellerShopifyPermission updateBasSellerShopifyPermission = new BasSellerShopifyPermission();
                updateBasSellerShopifyPermission.setLocations(locations);
                updateBasSellerShopifyPermission.setId(id);
                super.updateById(updateBasSellerShopifyPermission);
            }
        }
    }

    private String getLocations(String shop, String accessToken) {
        try {
            String url = "https://" + shop + "/admin/api/2022-04/locations.json";
            HttpRequest httpRequest = HttpUtil.createGet(url).header("X-Shopify-Access-Token", accessToken);
            HttpResponse httpResponse = httpRequest.execute();
            String body = httpResponse.body();
            logger.info("查询店铺地址信息返回结果，shop：{}，accessToken：{}，httpResponse：{}", shop, accessToken, body);
            if (StringUtils.isNotEmpty(body)) {
                JSONObject responseObject = JSONObject.parseObject(body);
                if (responseObject.containsKey("locations")) {
                    JSONArray locations = responseObject.getJSONArray("locations");
                    if (!locations.isEmpty()) {
                        StringJoiner joiner = new StringJoiner(",");
                        for (Object locationObject : locations) {
                            JSONObject location = (JSONObject) locationObject;
                            joiner.add(location.getString("id"));
                        }
                        return joiner.toString();
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Async
    @Override
    public void disabledByShop(String shop) {
        LambdaUpdateWrapper<BasSellerShopifyPermission> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
        lambdaUpdateWrapper.set(BasSellerShopifyPermission::getState, "2");
        lambdaUpdateWrapper.eq(BasSellerShopifyPermission::getShop, shop);
        super.update(lambdaUpdateWrapper);
    }
}
