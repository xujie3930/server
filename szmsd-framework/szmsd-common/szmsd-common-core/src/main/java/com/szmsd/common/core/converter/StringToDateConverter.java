package com.szmsd.common.core.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class StringToDateConverter extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String text = jsonParser.getText();
        Date date = null;
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            int len = text.length();
            if (len >= 14 && len <= 19) {
                date = getDateTimeFormat().parse(text);
            } else if (len >= 8 && len <= 10 && text.contains("-")) {
                date = getDateFormat().parse(text);
            } else if (len >= 5 && len <= 8 && text.contains(":")) {
                date = getTimeFormat().parse(text);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return date;
    }

    @Override
    public Class<?> handledType() {
        return Date.class;
    }

    private SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    private SimpleDateFormat getTimeFormat() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    private SimpleDateFormat getDateTimeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
}
