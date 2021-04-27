package com.szmsd.http.service.http;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 22:40
 */
public final class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    public static String get(Object object, String path) {
        Object co = object;
        for (String s : path.split("\\.")) {
            co = getValue(co, s);
        }
        return (String) co;
    }

    public static Object getValue(Object object, String field) {
        try {
            return MethodUtils.invokeMethod(object, getMethod(field));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getMethod(String field) {
        return ("get" + field.substring(0, 1).toUpperCase() + field.substring(1));
    }

    public static String formatApi(String api) {
        // api
        // base-info.seller         ->>> baseinfo.seller
        // base-info.shipment-rule  ->>> baseinfo.shipmentrule
        // exception.processing     ->>> exception.processing
        return api.trim().replace("-", "").toLowerCase();
    }
}
