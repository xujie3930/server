package com.szmsd.common.core.language.util;

import cn.hutool.core.codec.Base64;
import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class LanguageUtil {

    public static String language = "Language";

    public static String buildKey(String key) {
        return language + ":" + Base64.encode(key, "utf-8");
    }

    /**
     * Redis中获取value当前系统对应的语言翻译
     * @param isMultiple 是否多个,号分割
     * @param type RedisLanguageTable成员属性
     * @param value 需要转换的value
     * @return 转换后的结果
     */
    public static String getLanguage(boolean isMultiple, String type, String value) {
        return getLanguage(isMultiple, type, value, LanguageEnum.sysName);
    }

    /**
     * LocalLanguageTypeEnum LocalLanguageEnum当前系统对应的语言翻译
     * @param isMultiple 是否多个,号分割
     * @param typeEnum 对应的模板类型
     * @param value 需要转换的value
     * @return 转换后的结果
     */
    public static String getLanguage(boolean isMultiple, LocalLanguageTypeEnum typeEnum, String value) {
        return getLanguage(isMultiple, typeEnum, value, LanguageEnum.sysName);
    }

    /**
     * Redis或模板语言翻译 type String 从redis取， type LocalLanguageTypeEnum 从模板里取
     * @param isMultiple 是否多个,号分割
     * @param type RedisLanguageTable 或 LocalLanguageTypeEnum
     * @param value 需要转换的value
     * @return 转换后的结果
     */
    public static String getLanguage(boolean isMultiple, Object type, String value, LanguageEnum languageEnum) {
        if (!(type instanceof String) && !(type instanceof LocalLanguageTypeEnum)) {
            return value;
        }
        if (!isMultiple) {
            if (type instanceof String) {
                return getLanguage(type.toString(), value, languageEnum);
            }
            return getLanguage((LocalLanguageTypeEnum) type, value, languageEnum);
        }
        String[] split = value.split(",");
        List<String> sb = new ArrayList<>();
        for (String s : split) {
            String language = (type instanceof String) ? getLanguage(type.toString(), s, languageEnum) : getLanguage((LocalLanguageTypeEnum) type, value, languageEnum);
            language = StringUtils.isNotEmpty(language) ? language : s;
            sb.add(language);
        }
        return String.join(",", sb);
    }

    /**
     * 从Redis获取语言翻译
     * @param type RedisLanguageTable成员属性
     * @param value 需要转换的value
     * @return 转换后的结果
     */
    public static String getLanguage(String type, String value) {
        return getLanguage(SpringUtils.getBean("redisService"), type, value, LanguageEnum.sysName);
    }

    /**
     *
     * 从Redis获取语言翻译
     * @param type RedisLanguageTable成员属性
     * @param value 需要转换的value
     * @param languageEnum 语言
     * @return 转换后的结果
     */
    public static String getLanguage(String type, String value, LanguageEnum languageEnum) {
        return getLanguage(SpringUtils.getBean("redisService"), type, value, languageEnum);
    }

    /**
     * 从Redis获取语言翻译
     * @param redisService Redis
     * @param type RedisLanguageTable成员属性
     * @param value 需要转换的value
     * @param languageEnum 语言
     * @return 转换后的结果
     */
    public static String getLanguage(RedisService redisService, String type, String value, LanguageEnum languageEnum) {
        redisService = redisService != null ? redisService : SpringUtils.getBean("redisService");
        Map<String, Map<String, String>> cacheMap = redisService.getCacheMap(type);
        if (null != cacheMap && cacheMap.size() > 0) {
            Map<String, String> language = cacheMap.get(value);
            return language != null ? language.get(getLen(languageEnum)) : "";
        }
        return value;
    }

    /**
     * 从模板里获取语言翻译（系统语言）
     * @param typeEnum 模板
     * @param value 值
     * @return 转换后的结果
     */
    public static String getLanguage(LocalLanguageTypeEnum typeEnum, String value) {
        return getLanguage(typeEnum, value, LanguageEnum.sysName);
    }

    /**
     * 从模板里获取语言翻译（自定义语言）
     * @param typeEnum 模板
     * @param value 值
     * @param languageEnum 语言
     * @return 转换后的结果
     */
    public static String getLanguage(LocalLanguageTypeEnum typeEnum, String value, LanguageEnum languageEnum) {
        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(typeEnum, value);
        if (localLanguageEnum == null) {
            log.error("没有维护[{}]枚举语言[{}]", typeEnum, value);
            return value;
        }
        String language;
        if ("enName".equals(LanguageUtil.getLen(languageEnum))) {
            language = localLanguageEnum.getEhName();
        } else {
            language = localLanguageEnum.getZhName();
        }
        return language;
    }

    /**
     * 获取语言
     * @param languageEnum 语言枚举
     * @return 返还当前语言
     */
    public static String getLen(LanguageEnum languageEnum) {
        if (languageEnum != LanguageEnum.sysName) {
            return languageEnum.name();
        }
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isBlank(len)) {
            len = "zh";
        }
        return len.trim().toLowerCase().replaceAll("name","").concat("Name");
    }

}
