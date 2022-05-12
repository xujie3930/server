package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.domain.BasCk1ShopifyWebhooksLog;
import com.szmsd.bas.service.IBasCk1ShopifyWebhooksLogService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = {"Shopify-Webhooks接口"})
@RestController
@RequestMapping("/bas-shopify-webhooks")
public class BasShopifyWebhooksController extends BaseController {

    @Autowired
    private IBasCk1ShopifyWebhooksLogService ck1ShopifyWebhooksLogService;

    // 接口文档：https://shopify.dev/apps/webhooks/configuration/mandatory-webhooks
    // webhook相关文档：https://shopify.dev/apps/webhooks
    @PostMapping(value = "/data_request")
    public R<?> dataRequest(@RequestBody Map<String, String> map,
                            @RequestHeader(value = "X-Shopify-Topic") String topic,
                            @RequestHeader(value = "X-Shopify-Hmac-Sha256") String hmac,
                            @RequestHeader(value = "X-Shopify-Webhook-Id") String webhookId,
                            @RequestHeader(value = "X-Shopify-Shop-Domain") String shop,
                            @RequestHeader(value = "X-Shopify-API-Version") String apiVersion,
                            HttpServletRequest request) {
        String type = "customers/data_request";
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
        return R.ok();
    }

    @PostMapping(value = "/redact")
    public R<?> redact(@RequestBody Map<String, String> map,
                       @RequestHeader(value = "X-Shopify-Topic") String topic,
                       @RequestHeader(value = "X-Shopify-Hmac-Sha256") String hmac,
                       @RequestHeader(value = "X-Shopify-Webhook-Id") String webhookId,
                       @RequestHeader(value = "X-Shopify-Shop-Domain") String shop,
                       @RequestHeader(value = "X-Shopify-API-Version") String apiVersion,
                       HttpServletRequest request) {
        String type = "customers/redact";
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
        return R.ok();
    }

    @PostMapping(value = "/shop/redact")
    public R<?> shopRedact(@RequestBody Map<String, String> map,
                           @RequestHeader(value = "X-Shopify-Topic") String topic,
                           @RequestHeader(value = "X-Shopify-Hmac-Sha256") String hmac,
                           @RequestHeader(value = "X-Shopify-Webhook-Id") String webhookId,
                           @RequestHeader(value = "X-Shopify-Shop-Domain") String shop,
                           @RequestHeader(value = "X-Shopify-API-Version") String apiVersion,
                           HttpServletRequest request) {
        String type = "shop/redact";
        String payload = JSONObject.toJSONString(map);
        this.saveLog(type, payload, request);
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
