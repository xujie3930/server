package com.szmsd.http.componet;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.szmsd.http.annotation.ErrorSerializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ErrorSerializableImpl extends JsonSerializer<String> implements ContextualSerializer {

    public ErrorSerializableImpl() {
    }

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            Map<String, List<String>> errors = JSONObject.parseObject(value, new TypeReference<Map<String, List<String>>>(){});
            String collect = errors.entrySet().stream().map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.joining(","));
            gen.writeString(collect);
        } catch (Exception e) {
            gen.writeString(value);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        ErrorSerializable annotation = property.getAnnotation(ErrorSerializable.class);
        return prov.findValueSerializer(annotation.getClass());
    }

}
