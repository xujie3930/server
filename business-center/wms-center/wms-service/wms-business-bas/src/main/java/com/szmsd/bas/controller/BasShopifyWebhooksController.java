package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.config.HMacSHA256;
import com.szmsd.bas.config.ShopifyAppConfig;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;
import com.szmsd.bas.service.IBasCk1ShopifyWebhooksLogService;
import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.UnauthorizedException;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Api(tags = {"Shopify-Webhooks接口"})
@RestController
@RequestMapping("/bas-shopify-webhooks")
public class BasShopifyWebhooksController extends BaseController {

    @Autowired
    private ShopifyAppConfig shopifyAppConfig;
    @Autowired
    private IBasCk1ShopifyWebhooksLogService ck1ShopifyWebhooksLogService;
    @Autowired
    private IBasSellerShopifyPermissionService sellerShopifyPermissionService;

    // 接口文档：https://shopify.dev/apps/webhooks/configuration/mandatory-webhooks
    // webhook相关文档：https://shopify.dev/apps/webhooks
    @PostMapping(value = "/data_request")
    public R<?> dataRequest(@RequestBody Map<String, Object> map,
                            @RequestHeader(value = "X-Shopify-Topic", required = false) String topic,
                            @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmac,
                            @RequestHeader(value = "X-Shopify-Webhook-Id", required = false) String webhookId,
                            @RequestHeader(value = "X-Shopify-Shop-Domain", required = false) String shop,
                            @RequestHeader(value = "X-Shopify-API-Version", required = false) String apiVersion,
                            HttpServletRequest request) {
        String type = "customers/data_request";
        map.put("header", headerInfo(request));
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
        boolean verified = verifyWebhook(map, hmac);
        if (!verified) {
            throw new UnauthorizedException("签名验证失败");
        }
        return R.ok();
    }

    @PostMapping(value = "/redact")
    public R<?> redact(@RequestBody Map<String, Object> map,
                       @RequestHeader(value = "X-Shopify-Topic", required = false) String topic,
                       @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmac,
                       @RequestHeader(value = "X-Shopify-Webhook-Id", required = false) String webhookId,
                       @RequestHeader(value = "X-Shopify-Shop-Domain", required = false) String shop,
                       @RequestHeader(value = "X-Shopify-API-Version", required = false) String apiVersion,
                       HttpServletRequest request) {
        String type = "customers/redact";
        map.put("header", headerInfo(request));
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
        boolean verified = verifyWebhook(map, hmac);
        if (!verified) {
            throw new UnauthorizedException("签名验证失败");
        }
        return R.ok();
    }

    public boolean verifyWebhook(Map<String, Object> map, String hmac) {
        if (null == hmac) {
            return false;
        }
        String jsonData = JSON.toJSONString(map);
        String encryptBase64 = HMacSHA256.encryptBase64(this.shopifyAppConfig.getClientSecret(), jsonData);
        return hmac.equals(encryptBase64);
    }

    public Map<String, Object> headerInfo(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String element = headerNames.nextElement();
            String header = request.getHeader(element);
            headerMap.put(element, header);
        }
        return headerMap;
    }

    // 客户卸载应用48小时后，shopify会推送该接口
    @PostMapping(value = "/shop/redact")
    public R<?> shopRedact(@RequestBody Map<String, Object> map,
                           @RequestHeader(value = "X-Shopify-Topic", required = false) String topic,
                           @RequestHeader(value = "X-Shopify-Hmac-Sha256", required = false) String hmac,
                           @RequestHeader(value = "X-Shopify-Webhook-Id", required = false) String webhookId,
                           @RequestHeader(value = "X-Shopify-Shop-Domain", required = false) String shop,
                           @RequestHeader(value = "X-Shopify-API-Version", required = false) String apiVersion,
                           HttpServletRequest request) {
        String type = "shop/redact";
        map.put("header", headerInfo(request));
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
        boolean verified = verifyWebhook(map, hmac);
        if (!verified) {
            throw new UnauthorizedException("签名验证失败");
        }
        /*
        {
          "shop_id": 954889,
          "shop_domain": "{shop}.myshopify.com"
        }
         */
        String shopDomain = (String) map.get("shop_domain");
        if (StringUtils.isNotEmpty(shopDomain)) {
            this.sellerShopifyPermissionService.disabledByShop(shopDomain);
        }
        return R.ok();
    }

    private void saveLog(String type, String payload, HttpServletRequest request) {
        BasCk1ShopifyWebhooksLog ck1ShopifyWebhooksLog = new BasCk1ShopifyWebhooksLog();
        ck1ShopifyWebhooksLog.setType(type);
        ck1ShopifyWebhooksLog.setPayload(payload);
        ck1ShopifyWebhooksLog.setWebhookId(request.getHeader("X-Shopify-Webhook-Id"));
        ck1ShopifyWebhooksLog.setHmac(request.getHeader("X-Shopify-Hmac-Sha256"));
        ck1ShopifyWebhooksLog.setShop(request.getHeader("X-Shopify-Shop-Domain"));
        ck1ShopifyWebhooksLog.setApiVersion(request.getHeader("X-Shopify-API-Version"));
        ck1ShopifyWebhooksLog.setTopic(request.getHeader("X-Shopify-Topic"));
        this.ck1ShopifyWebhooksLogService.saveLog(ck1ShopifyWebhooksLog);
    }
}
