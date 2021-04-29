package com.szmsd.common.core.language.componet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.utils.ServletUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;

/**
 * 序列化语言
 *
 * @author MSD
 * @date 2021-03-02
 */
@Component
@Slf4j
public class FieldLanguage extends JsonSerializer<String> implements ContextualSerializer {

    @Resource
    private RedisService redisService;

    private String jsonI18nType;

    private LanguageEnum jsonI18nLanguage;

    private String jsonI18nValue;

    private LocalLanguageTypeEnum jsonI18nLocalLanguage;

    private boolean jsonI18nIsPlaceholder;

    public FieldLanguage() {
    }

    public FieldLanguage(String jsonI18nType, LanguageEnum jsonI18nLanguage, String jsonI18nValue, LocalLanguageTypeEnum jsonI18nLocalLanguage, boolean jsonI18nIsPlaceholder, RedisService redisService) {
        this.jsonI18nType = jsonI18nType;
        this.jsonI18nLanguage = jsonI18nLanguage;
        this.jsonI18nValue = jsonI18nValue;
        this.jsonI18nLocalLanguage = jsonI18nLocalLanguage;
        this.jsonI18nIsPlaceholder = jsonI18nIsPlaceholder;
        this.redisService = redisService;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            if (jsonI18nLocalLanguage != LocalLanguageTypeEnum.SYSTEM_LANGUAGE) {
                gen.writeString(getLocalLanguage(value));
                return;
            } else if (StringUtils.isNotEmpty(jsonI18nType)) {
                String name = getLanguage(jsonI18nType, value);
                gen.writeString(name);
            } else {
                gen.writeString(jsonI18nValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            gen.writeString(jsonI18nValue);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        FieldJsonI18n annotation = property.getAnnotation(FieldJsonI18n.class);
        if (annotation == null) {
            return prov.findNullValueSerializer(property);
        }
        return new FieldLanguage(annotation.type(), annotation.language(), annotation.value(), annotation.localLanguageType(), annotation.isPlaceholder(), null == this.redisService ? SpringUtils.getBean("redisService") : this.redisService);
    }

    private String getLanguage(String type, String code) {
        Map<String, Map<String, String>> cacheMap = redisService.getCacheMap(type);
        if (null != cacheMap && cacheMap.size() > 0) {
            Map<String, String> language = cacheMap.get(code);
            return language != null ? language.get(getLen()) : "";
        }
        return code;
    }

    private String getLocalLanguage(String value) {
        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(jsonI18nLocalLanguage, value);
        if (jsonI18nIsPlaceholder && value.contains("&")) {
            String[] split = value.split("&");
            localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(jsonI18nLocalLanguage, split[0]);
            value = split[1];
        }
        if (localLanguageEnum == null) {
            log.error("没有维护[{}]枚举语言[{}]", jsonI18nLocalLanguage, value);
            return value;
        }
        String language;
        switch (getLen()) {
            case "enName":
                language = localLanguageEnum.getEhName();
                break;
            default:
                language = localLanguageEnum.getZhName();
            break;
        }
        return jsonI18nIsPlaceholder ? MessageFormat.format(language, value.split(",")) : language;
    }

    private String getLen() {
        if (jsonI18nLanguage != LanguageEnum.sysName) {
            return jsonI18nLanguage.name();
        }
        String len = ServletUtils.getHeaders("Langr");
        if (StringUtils.isEmpty(len)) {
            len = "zh";
        }
        return len.trim().toLowerCase().concat("Name");
    }

}
