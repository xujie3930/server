package com.szmsd.common.security.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Description:
 * @Author
 * @Date
 **/
public class MyOauthExceptionSerializer extends StdSerializer<MyOauthException> {

    protected MyOauthExceptionSerializer() {
        super(MyOauthException.class);
    }

    @Override
    public void serialize(MyOauthException e, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("code", String.valueOf(e.getHttpErrorCode()));
        jsonGenerator.writeStringField("msg", e.getMessage());
        jsonGenerator.writeStringField("data", null);
        jsonGenerator.writeEndObject();
    }
}
