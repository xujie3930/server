package com.szmsd.finance.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.logging.LoggerFactory;

/**
 * @ClassName: LogUtil
 * @Description:
 * @Author: 11
 * @Date: 2021-11-22 10:53
 */

public final class LogUtil {

    public static String format(String prefix, Object targetObj) {
        return ("【" + prefix + "】 --- " + JSONObject.toJSONString(targetObj));
    }

    public static String format(Object targetObj, String... prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : prefix) {
            stringBuilder.append("【").append(s).append("】").append("#");
        }
        stringBuilder.append("Param:---");
        return stringBuilder.append(JSONObject.toJSONString(targetObj)).toString();
    }
}
