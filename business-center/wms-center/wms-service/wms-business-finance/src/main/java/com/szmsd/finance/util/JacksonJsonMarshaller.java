package com.szmsd.finance.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JacksonJsonMarshaller implements HeliMarshaller {

	private static ObjectMapper objectMapper;

	@Override
	public <T> T unmarshal(String content, Class<T> objectType) {
		try {
			return getObjectMapper().readValue(content, objectType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public ObjectMapper getObjectMapper() throws IOException {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
			objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			objectMapper.setSerializationInclusion(Include.NON_EMPTY);
			objectMapper
					.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
		}
		return objectMapper;
	}
}
