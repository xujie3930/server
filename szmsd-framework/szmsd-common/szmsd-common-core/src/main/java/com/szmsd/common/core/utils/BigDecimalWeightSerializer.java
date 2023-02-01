package com.szmsd.common.core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 重量统一保留三位小数，向上取整
 */
public class BigDecimalWeightSerializer extends JsonSerializer<BigDecimal> {

    public BigDecimalWeightSerializer(){

    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (value != null) {
            BigDecimal number = value.setScale(3, BigDecimal.ROUND_UP);
            gen.writeNumber(number);
        } else {
            gen.writeNumber(value);
        }
    }
}
