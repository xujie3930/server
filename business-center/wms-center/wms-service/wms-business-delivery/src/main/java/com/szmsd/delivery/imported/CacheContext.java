package com.szmsd.delivery.imported;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:49
 */
public interface CacheContext<K, V> {

    void put(K k, V v);

    V get(K k);

    boolean containsKey(K k);

    V remove(K k);

    void clear();

    /**
     * cache context
     */
    class MapCacheContext<K, V> implements CacheContext<K, V> {
        private final Map<K, V> map = new HashMap<>();

        @Override
        public void put(K k, V v) {
            this.map.put(k, v);
        }

        @Override
        public V get(K k) {
            return this.map.get(k);
        }

        @Override
        public boolean containsKey(K k) {
            return this.map.containsKey(k);
        }

        @Override
        public V remove(K k) {
            return this.map.remove(k);
        }

        @Override
        public void clear() {
            this.map.clear();
        }
    }
}
