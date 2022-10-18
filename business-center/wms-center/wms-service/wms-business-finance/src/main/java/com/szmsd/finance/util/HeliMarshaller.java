package com.szmsd.finance.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface HeliMarshaller {
	/**
	 * 将字符串反序列化为相应的对象
	 */
	<T> T unmarshal(String content, Class<T> objectType);

	/**
	 * 返回ObjectMapper
	 */
	ObjectMapper getObjectMapper() throws IOException;
}
