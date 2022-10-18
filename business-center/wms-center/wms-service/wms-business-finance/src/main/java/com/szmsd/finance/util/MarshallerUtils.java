package com.szmsd.finance.util;

import com.szmsd.finance.vo.helibao.FormatType;

import java.util.HashMap;
import java.util.Map;

public class MarshallerUtils {


    private static Map<FormatType, HeliMarshaller> marshallers = new HashMap<FormatType, HeliMarshaller>();

    static {
        marshallers.put(FormatType.json, new JacksonJsonMarshaller());
    }

    public static <T> T unmarshal(FormatType format, String content, Class<T> objectType) {
        HeliMarshaller marshaller = getMarshaller(format);
        return marshaller.unmarshal(content, objectType);
    }
    public static HeliMarshaller getMarshaller(FormatType format) {
        HeliMarshaller marshaller = marshallers.get(format);
        if (marshaller == null) {
            marshaller = marshallers.get(FormatType.json);
        }
        return marshaller;
    }


}
