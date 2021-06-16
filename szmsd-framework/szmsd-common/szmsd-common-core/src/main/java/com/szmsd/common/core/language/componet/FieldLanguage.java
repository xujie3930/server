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
import com.szmsd.common.core.language.util.LanguageUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private boolean isMultiple;

    public FieldLanguage() {
    }

    public FieldLanguage(String jsonI18nType, LanguageEnum jsonI18nLanguage, String jsonI18nValue, LocalLanguageTypeEnum jsonI18nLocalLanguage, boolean isMultiple, RedisService redisService) {
        this.jsonI18nType = jsonI18nType;
        this.jsonI18nLanguage = jsonI18nLanguage;
        this.jsonI18nValue = jsonI18nValue;
        this.jsonI18nLocalLanguage = jsonI18nLocalLanguage;
        this.isMultiple = isMultiple;
        this.redisService = redisService;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            if (jsonI18nLocalLanguage != LocalLanguageTypeEnum.SYSTEM_LANGUAGE) {
                gen.writeString(getLocalLanguage(value, isMultiple));
            } else if (StringUtils.isNotEmpty(jsonI18nType)) {
                String name = getLanguage(jsonI18nType, value, isMultiple);
                gen.writeString(name);
            } else {
                gen.writeString(jsonI18nValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        FieldJsonI18n annotation = property.getAnnotation(FieldJsonI18n.class);
        if (annotation == null) {
            return prov.findNullValueSerializer(property);
        }
        return new FieldLanguage(annotation.type(), annotation.language(), annotation.value(), annotation.localLanguageType(), annotation.isMultiple(), this.redisService);
    }

    private String getLanguage(String type, String value, boolean isMultiple) {
        if (!isMultiple) {
            return LanguageUtil.getLanguage(redisService, type, value);
        }
        String[] split = value.split(",");
        List<String> sb = new ArrayList<>();
        for (String s : split) {
            String language = LanguageUtil.getLanguage(redisService, type, s);
            language = StringUtils.isNotEmpty(language) ? language : s;
            sb.add(language);
        }
        return String.join(",", sb);
    }

    private String getLocalLanguage(String value, boolean isMultiple) {
        if (!isMultiple) {
            return getLocalLanguage(value);
        }
        String[] split = value.split(",");
        List<String> sb = new ArrayList<>();
        for (String s : split) {
            String language = getLocalLanguage(s);
            language = StringUtils.isNotEmpty(language) ? language : s;
            sb.add(language);
        }
        return String.join(",", sb);
    }
    private String getLocalLanguage(String value) {
        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(jsonI18nLocalLanguage, value);
        if (localLanguageEnum == null) {
            log.error("没有维护[{}]枚举语言[{}]", jsonI18nLocalLanguage, value);
            return value;
        }
        String language;
        switch (LanguageUtil.getLen(jsonI18nLanguage)) {
            case "enName":
                language = localLanguageEnum.getEhName();
                break;
            default:
                language = localLanguageEnum.getZhName();
                break;
        }
        return language;
    }

}
