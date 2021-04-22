package com.szmsd.open.interceptor;

import com.szmsd.open.config.AuthConfig;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 16:03
 */
@Component
public class ConfigAuthHandler implements AuthHandler {

    @Autowired
    private AuthConfig authConfig;

    @Override
    public void authentication(String appId, String sign) throws AuthHandlerException {
        // 帐号或密码是空的
        if (StringUtils.isEmpty(appId)
                || StringUtils.isEmpty(sign)) {
            throw new AuthHandlerException("帐号密码不能为空");
        }
        // 系统没有配置帐号密码信息
        Map<String, String> accountMap = this.authConfig.getAccountMap();
        if (MapUtils.isEmpty(accountMap)) {
            throw new AuthHandlerException("系统未配置认证信息");
        }
        // 帐号未配置
        if (!accountMap.containsKey(appId)) {
            throw new AuthHandlerException("帐号信息无效");
        }
        // 密码不对
        if (!sign.equals(accountMap.get(appId))) {
            throw new AuthHandlerException("帐号密码错误");
        }
    }
}
