package com.szmsd.common.core.language.util;

import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;

import java.util.Map;

public class LanguageUtil {

    public static String getLanguage(String type, String value) {
        return getLanguage(SpringUtils.getBean("redisService"), type, value, LanguageEnum.sysName);
    }

    public static String getLanguage(RedisService redisService, String type, String value) {
        return getLanguage(redisService, type, value, LanguageEnum.sysName);
    }

    public static String getLanguage(RedisService redisService, String type, String value, LanguageEnum languageEnum) {
        redisService = redisService != null ? redisService : SpringUtils.getBean("redisService");
        Map<String, Map<String, String>> cacheMap = redisService.getCacheMap(type);
        if (null != cacheMap && cacheMap.size() > 0) {
            Map<String, String> language = cacheMap.get(value);
            return language != null ? language.get(getLen(languageEnum)) : "";
        }
        return value;
    }

    public static String getLen(LanguageEnum languageEnum) {
        if (languageEnum != LanguageEnum.sysName) {
            return languageEnum.name();
        }
        String len = ServletUtils.getHeaders("Langr") + "";
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        return len.trim().toLowerCase().concat("Name");
    }

}
