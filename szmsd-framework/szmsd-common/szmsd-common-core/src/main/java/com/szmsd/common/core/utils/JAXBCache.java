package com.szmsd.common.core.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liyingfeng
 * @date 2020/9/11 14:03
 */
public class JAXBCache {
    private static final JAXBCache instance = new JAXBCache();

    private final ConcurrentMap<String, JAXBContext> contextCache = new ConcurrentHashMap<String, JAXBContext>();

    private JAXBCache() {
    }

    public static JAXBCache instance() {
        return instance;
    }

    JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
        JAXBContext context = contextCache.get(clazz.getName());
        if ( context == null )
        {
            context = JAXBContext.newInstance(clazz);
            contextCache.putIfAbsent(clazz.getName(), context);
        }
        return context;
    }
}
