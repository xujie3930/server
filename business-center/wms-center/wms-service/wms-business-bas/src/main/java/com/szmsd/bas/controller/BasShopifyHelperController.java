package com.szmsd.bas.controller;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.config.HMacSHA256;
import com.szmsd.bas.config.ShopifyAppConfig;
import com.szmsd.bas.domain.BasCk1ShopifyLog;
import com.szmsd.bas.service.IBasCk1ShopifyLogService;
import com.szmsd.bas.service.IBasSellerShopifyPermissionService;
import com.szmsd.bas.util.ShopifyUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Api(tags = {"Shopify接口"})
@RestController
@RequestMapping("/bas-shopify-helper")
public class BasShopifyHelperController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(BasShopifyHelperController.class);

    @Autowired
    private ShopifyAppConfig shopifyAppConfig;
    @Autowired
    private Environment environment;
    @Autowired
    private IBasCk1ShopifyLogService ck1ShopifyLogService;
    @Autowired
    private IBasSellerShopifyPermissionService sellerShopifyPermissionService;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @GetMapping(value = "/config")
    public R<?> shopifyAppConfig(@RequestParam(value = "configKey", required = false) String configKey) {
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(this.shopifyAppConfig);
        if (null == jsonObject) {
            jsonObject = new JSONObject();
        }
        if (StringUtils.isNotEmpty(configKey)) {
            String[] keyArray = configKey.split(",");
            for (String key : keyArray) {
                jsonObject.put(key, environment.getProperty(key));
            }
        }
        return R.ok(jsonObject);
    }

    @GetMapping(value = "/encryptHex")
    public R<String> encryptHex(@RequestParam(value = "secret") String secret,
                                @RequestParam(value = "message") String message) {
        String encrypt = HMacSHA256.encryptHex(secret, message);
        return R.ok(encrypt);
    }

    @GetMapping(value = "/encryptBase64")
    public R<String> encryptBase64(@RequestParam(value = "secret") String secret,
                                   @RequestParam(value = "message") String message) {
        String encrypt = HMacSHA256.encryptBase64(secret, message);
        return R.ok(encrypt);
    }

    // hmac=4ae35e82d0330c234fc48ac744bc275637a9499d82603575ae26132b82d451cd
    // &host=dGVzdC1kbS1mdWxmaWxsbWVudC5teXNob3BpZnkuY29tL2FkbWlu
    // &shop=test-dm-fulfillment.myshopify.com
    // &timestamp=1652236914
    @GetMapping(value = "/scopes")
    public R<String> shopifyScopes(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestParam(value = "hmac", required = false) String hmac,
                                   @RequestParam(value = "host", required = false) String host,
                                   @RequestParam(value = "shop", required = false) String shop,
                                   @RequestParam(value = "timestamp", required = false) String timestamp) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        String encryptHex = ShopifyUtil.encryptParameter(parameterMap, this.shopifyAppConfig.getClientSecret());
        // https://{shop}.myshopify.com/admin/oauth/authorize?client_id={api_key}&scope={scopes}&redirect_uri={redirect_uri}&state={nonce}&grant_options[]={access_mode}
        String oauthAuthorize = this.shopifyAppConfig.getOauthAuthorize();
        Map<String, String> variableMap = new HashMap<>();
        // replace .myshopify.com
        String shopName = shop.replace(".myshopify.com", "");
        variableMap.put("shop", shopName);
        variableMap.put("api_key", this.shopifyAppConfig.getClientId());
        variableMap.put("scopes", this.shopifyAppConfig.getScope());
        variableMap.put("redirect_uri", this.shopifyAppConfig.getRedirectUri());
        String nonce = "" + (System.nanoTime());
        variableMap.put("nonce", nonce);
        variableMap.put("access_mode", "value");
        for (String key : variableMap.keySet()) {
            oauthAuthorize = oauthAuthorize.replaceAll("\\{" + key + "}", variableMap.get(key));
        }
        // 记录日志
        BasCk1ShopifyLog ck1ShopifyLog = new BasCk1ShopifyLog();
        ck1ShopifyLog.setType("1");
        ck1ShopifyLog.setHmac(hmac);
        ck1ShopifyLog.setState(nonce);
        ck1ShopifyLog.setHost(host);
        ck1ShopifyLog.setShop(shop);
        ck1ShopifyLog.setTimestamp(timestamp);
        ck1ShopifyLog.setOtherParams(JSONObject.toJSONString(parameterMap));
        ck1ShopifyLog.setClientId(variableMap.get("api_key"));
        ck1ShopifyLog.setScope(variableMap.get("scopes"));
        ck1ShopifyLog.setRedirectUri(oauthAuthorize);
        ck1ShopifyLog.setVerifyState(String.valueOf(encryptHex.equals(hmac)));
        this.ck1ShopifyLogService.saveLog(ck1ShopifyLog);
        try {
            // 存入redis
            String key = "shopify:app:" + shopName;
            this.redisTemplate.opsForHash().put(key, "state", nonce);
            this.redisTemplate.opsForHash().put(key, "scope", variableMap.get("scopes"));
            this.redisTemplate.expire(key, 10, TimeUnit.MINUTES);
            response.setHeader("content-security-policy", "frame-ancestors " + shop + " https://admin.shopify.com;");
            // 重定向
            response.sendRedirect(oauthAuthorize);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return R.ok(oauthAuthorize);
    }

    // code=dfa03ea98ce4679b4cb5b4fc67d319d0
    // &hmac=00903569aaba929eb91c57dee6f79eec4f4413a19f2d587cf11b2ac3ebf8154e
    // &host=dGVzdC1kbS1mdWxmaWxsbWVudC5teXNob3BpZnkuY29tL2FkbWlu
    // &shop=test-dm-fulfillment.myshopify.com
    // &state=20220511104154
    // &timestamp=1652237081
    @PostMapping(value = "/activate")
    public R<?> activate(@RequestBody Map<String, String> map) {
        LinkedHashMap<String, String[]> parameterMap = new LinkedHashMap<>();
        String code;
        String hmac = map.get("hmac");
        String host;
        String shop;
        String state;
        String timestamp;
        parameterMap.put("code", new String[]{code = map.get("code")});
        parameterMap.put("host", new String[]{host = map.get("host")});
        parameterMap.put("shop", new String[]{shop = map.get("shop")});
        parameterMap.put("state", new String[]{state = map.get("state")});
        parameterMap.put("timestamp", new String[]{timestamp = map.get("timestamp")});
        String encryptHex = ShopifyUtil.encryptParameter(parameterMap, this.shopifyAppConfig.getClientSecret());
        // 记录日志
        BasCk1ShopifyLog ck1ShopifyLog = new BasCk1ShopifyLog();
        ck1ShopifyLog.setType("2");
        ck1ShopifyLog.setCode(code);
        ck1ShopifyLog.setHmac(hmac);
        ck1ShopifyLog.setState(state);
        ck1ShopifyLog.setHost(host);
        ck1ShopifyLog.setShop(shop);
        ck1ShopifyLog.setTimestamp(timestamp);
        String otherParams = JSONObject.toJSONString(parameterMap);
        ck1ShopifyLog.setOtherParams(otherParams);
        if (null == encryptHex) {
            encryptHex = "-";
        }
        ck1ShopifyLog.setVerifyState(String.valueOf(encryptHex.equals(hmac)));
        this.ck1ShopifyLogService.saveLog(ck1ShopifyLog);
        // 存入redis
        String shopifyUUID = UUID.randomUUID().toString();
        String key = "shopify:activate:" + shopifyUUID;
        this.redisTemplate.opsForValue().set(key, JSONObject.toJSONString(ck1ShopifyLog), 10, TimeUnit.MINUTES);
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("shopifyUUID", shopifyUUID);
        resultMap.put("matcher", "shopify");
        return R.ok(resultMap);
    }

    // shopifyUUID
    // username
    @PostMapping(value = "/login")
    public R<?> login(@RequestBody Map<String, String> map) {
        // 从redis获取
        String shopifyUUID = map.get("shopifyUUID");
        String activateKey = "shopify:activate:" + shopifyUUID;
        Object object = this.redisTemplate.opsForValue().get(activateKey);
        JSONObject jsonObject = JSONObject.parseObject((String) object);
        if (null == jsonObject) {
            jsonObject = new JSONObject();
        }
        jsonObject.put("username", map.get("username"));
        LinkedHashMap<String, String[]> parameterMap = new LinkedHashMap<>();
        String code;
        String hmac = jsonObject.getString("hmac");
        String host;
        String shop;
        String state;
        String timestamp;
        parameterMap.put("code", new String[]{code = jsonObject.getString("code")});
        parameterMap.put("host", new String[]{host = jsonObject.getString("host")});
        parameterMap.put("shop", new String[]{shop = jsonObject.getString("shop")});
        parameterMap.put("state", new String[]{state = jsonObject.getString("state")});
        parameterMap.put("timestamp", new String[]{timestamp = jsonObject.getString("timestamp")});
        parameterMap.put("username", new String[]{map.get("username")});
        // replace .myshopify.com
        String shopName = shop.replace(".myshopify.com", "");
        String key = "shopify:app:" + shopName;
        Object cacheState = this.redisTemplate.opsForHash().get(key, "state");
        if (null == cacheState) {
            cacheState = "-";
        }
        if (!cacheState.equals(state)) {
            throw new CommonException("500", "校验失败，state错误");
        }
        Object cacheScope = this.redisTemplate.opsForHash().get(key, "scope");
        String encryptHex = ShopifyUtil.encryptParameter(parameterMap, this.shopifyAppConfig.getClientSecret());
        jsonObject.put("scope", String.valueOf(cacheScope));
        // 记录日志
        BasCk1ShopifyLog ck1ShopifyLog = new BasCk1ShopifyLog();
        ck1ShopifyLog.setType("3");
        ck1ShopifyLog.setCode(code);
        ck1ShopifyLog.setHmac(hmac);
        ck1ShopifyLog.setState(state);
        ck1ShopifyLog.setHost(host);
        ck1ShopifyLog.setShop(shop);
        ck1ShopifyLog.setTimestamp(timestamp);
        ck1ShopifyLog.setOtherParams(JSONObject.toJSONString(parameterMap));
        if (null == encryptHex) {
            encryptHex = "-";
        }
        ck1ShopifyLog.setVerifyState(String.valueOf(encryptHex.equals(hmac)));
        this.ck1ShopifyLogService.saveLog(ck1ShopifyLog);
        this.sellerShopifyPermissionService.getAccessToken(jsonObject);
        return R.ok();
    }
}
