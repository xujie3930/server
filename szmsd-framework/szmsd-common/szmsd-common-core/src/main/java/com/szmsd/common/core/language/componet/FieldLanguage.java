package com.szmsd.common.core.language.componet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.common.core.language.util.LanguageUtil;
import com.szmsd.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 序列化语言
 *
 * @author MSD
 * @date 2021-03-02
 */
@Component
@Slf4j
public class FieldLanguage extends JsonSerializer<String> implements ContextualSerializer {

    private String jsonI18nType;

    private LanguageEnum jsonI18nLanguage;

    private String jsonI18nValue;

    private LocalLanguageTypeEnum jsonI18nLocalLanguage;

    private boolean isMultiple;

    public FieldLanguage() {
    }

    public FieldLanguage(String jsonI18nType, LanguageEnum jsonI18nLanguage, String jsonI18nValue, LocalLanguageTypeEnum jsonI18nLocalLanguage, boolean isMultiple) {
        this.jsonI18nType = jsonI18nType;
        this.jsonI18nLanguage = jsonI18nLanguage;
        this.jsonI18nValue = jsonI18nValue;
        this.jsonI18nLocalLanguage = jsonI18nLocalLanguage;
        this.isMultiple = isMultiple;
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            if (jsonI18nLocalLanguage != LocalLanguageTypeEnum.SYSTEM_LANGUAGE) {
                gen.writeString(LanguageUtil.getLanguage(isMultiple, jsonI18nLocalLanguage, value, jsonI18nLanguage));
            } else if (StringUtils.isNotEmpty(jsonI18nType)) {
                String name = LanguageUtil.getLanguage(isMultiple, jsonI18nType, value);
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
        return new FieldLanguage(annotation.type(), annotation.language(), annotation.value(), annotation.localLanguageType(), annotation.isMultiple());
    }

}
