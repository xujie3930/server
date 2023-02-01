package com.szmsd.common.core.utils;

import com.alibaba.fastjson.serializer.BeanContext;
import com.alibaba.fastjson.serializer.BigDecimalCodec;
import com.alibaba.fastjson.serializer.ContextObjectSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalPriceCodec extends BigDecimalCodec implements ContextObjectSerializer {

    public static final BigDecimalPriceCodec instance = new BigDecimalPriceCodec();


    @Override
    public void write(JSONSerializer serializer, Object object, BeanContext context) throws IOException {
        String format = StringUtils.isNotBlank(context.getFormat()) ? context.getFormat() : "#0.00";

        if(object != null){
           String s = new DecimalFormat(format).format(object);
           BigDecimal value = new BigDecimal(s);
           BigDecimal number = value.setScale(2, BigDecimal.ROUND_UP);
            serializer.out.writeString(number.toString());
        }else {
            serializer.out.writeString("0.00");
        }
    }
}
