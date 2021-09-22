package com.szmsd.doc.validator;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component(value = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN)
public class WarDictionaryPlugin implements DictionaryPlugin {

    private final LRUCache<String, String> lruCache;
    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    public WarDictionaryPlugin() {
        this.lruCache = CacheUtil.newLRUCache(32, (8 * 3600 * 1000));
    }

    @Override
    public boolean valid(Object value, String param) {
        // value是仓库编码
        String s = String.valueOf(value);
        String v = this.lruCache.get(s);
        if (null == v) {
            BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(s);
            if (null == basWarehouse) {
                return false;
            }
            v = s;
            this.lruCache.put(s, v);
        }
        return Objects.nonNull(v);
    }
}
