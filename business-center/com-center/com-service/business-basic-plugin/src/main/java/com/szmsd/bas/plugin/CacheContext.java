package com.szmsd.bas.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:49
 */
public interface CacheContext {

    /**
     * set
     *
     * @param key   key
     * @param value value
     */
    void set(Object key, Object value);

    /**
     * get
     *
     * @param key key
     * @return value
     */
    Object get(Object key);

    /**
     * containsKey
     *
     * @param key key
     * @return boolean
     */
    boolean containsKey(Object key);

    /**
     * remove
     *
     * @param key key
     * @return Serializable
     */
    Object remove(Object key);

    /**
     * clear
     */
    void clear();

    /**
     * cache context
     */
    class HandlerCacheContext implements CacheContext {
        private final Map<Object, Object> map = new HashMap<>();

        @Override
        public void set(Object key, Object value) {
            this.map.put(key, value);
        }

        @Override
        public Object get(Object key) {
            return this.map.get(key);
        }

        @Override
        public boolean containsKey(Object key) {
            return this.map.containsKey(key);
        }

        @Override
        public Object remove(Object key) {
            return this.map.remove(key);
        }

        @Override
        public void clear() {
            this.map.clear();
        }
    }
}
