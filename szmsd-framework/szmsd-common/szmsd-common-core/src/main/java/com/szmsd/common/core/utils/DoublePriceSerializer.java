package com.szmsd.common.core.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

public class DoublePriceSerializer extends JsonSerializer<Double> {

    public DoublePriceSerializer(){

    }

    @Override
    public void serialize(Double aDouble, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        if (aDouble != null) {

            BigDecimal value = new BigDecimal(aDouble);
            BigDecimal number = value.setScale(2, BigDecimal.ROUND_UP);
            gen.writeNumber(number.doubleValue());
        } else {
            gen.writeNumber(aDouble);
        }
    }
}
