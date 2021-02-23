package com.szmsd.common.core.exception.com;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @FileName LogisticsExceptionUtil.java
 * @Description 异常工具类
 * @Date 2020-06-15 14:07
 * @Author Yan Hua
 * @Version 1.0
 */
@Component
@Slf4j
public class LogisticsExceptionUtil {

    /**
     * @param exceptionMessageEnum
     * @return
     * @description 消息提示内容无对应变量值
     */
    public static LogisticsException getException(ExceptionMessageEnum exceptionMessageEnum, String code) {
        String key = exceptionMessageEnum.getKey();
        return new LogisticsException(key, getAndReplaceMessage(ExceptionMessageEnum.getValueByCode(key, code)));
    }

    /**
     * @param exceptionMessageEnum
     * @param values               泛型
     * @return
     * @description 消息提示内容带对应多变量值
     */
    public static LogisticsException getException(ExceptionMessageEnum exceptionMessageEnum, String code, Object... values) {
        String key = exceptionMessageEnum.getKey();
        return new LogisticsException(key, getAndReplaceMessage(ExceptionMessageEnum.getValueByCode(key, code), values));
    }

    /**
     * @param messageText
     * @param values
     * @return
     * @description 消息变量通配符赋值
     */
    private static String getAndReplaceMessage(String messageText, Object... values) {
        if (values == null || values.length < 1) {
            return messageText;
        }
        for (int i = 0; i < values.length; i++) {
            int index = i + 1;
            messageText = messageText.replace("&" + index, String.valueOf(values[i]));
        }
        return messageText;
    }

    public static void main(String[] args) {
        LogisticsException logisticsException = getException(ExceptionMessageEnum.EXPBASIS001, "ar");
        System.out.println("1:"+logisticsException.getMessage());
        System.out.println("2:"+getException(ExceptionMessageEnum.EXPORDER002, "zh", "订单管理3424", "对方30003240003").getMessage());


        JSONObject jo = (JSONObject) JSONObject.toJSON(getException(ExceptionMessageEnum.EXPBASIS001, "en"));
        System.out.println("3:"+jo.toString());


    }

}
