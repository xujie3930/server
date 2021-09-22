package com.szmsd.doc.validator;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import com.szmsd.bas.api.client.BasSubClientService;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component(value = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN)
public class SubDictionaryPlugin implements DictionaryPlugin {

    private final LRUCache<String, List<BasSubWrapperVO>> lruCache;
    @Autowired
    private BasSubClientService basSubClientService;

    public SubDictionaryPlugin() {
        this.lruCache = CacheUtil.newLRUCache(32, (8 * 3600 * 1000));
    }

    @Override
    public boolean valid(Object value, String param) {
        if (null == param || "".equals(param)) {
            return false;
        }
        // value是字段的值，支持单个属性，集合属性，示例：xxx，[xxx1, xxx2]
        // param是数据字典的编码
        // param格式，001
        // param格式，&&001
        // param开头携带&&表示，数据字典取值subValue，否则取值subCode
        boolean subValue;
        if (param.startsWith("&&")) {
            subValue = true;
            param = param.substring(2);
        } else {
            subValue = false;
        }
        List<BasSubWrapperVO> voList = this.lruCache.get(param);
        if (null == voList) {
            Map<String, List<BasSubWrapperVO>> map = this.basSubClientService.getSub(param);
            if (null == map) {
                return false;
            }
            voList = map.get(param);
            if (null == voList) {
                return false;
            }
            this.lruCache.put(param, voList);
        }
        List<String> valueList = this.getValueList(value);
        Set<String> set = new HashSet<>(voList.size());
        for (BasSubWrapperVO vo : voList) {
            if (subValue) {
                set.add(vo.getSubValue());
            } else {
                set.add(vo.getSubCode());
            }
        }
        for (String itemValue : valueList) {
            if (!set.contains(itemValue)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings({"unchecked"})
    private List<String> getValueList(Object value) {
        if (value instanceof List) {
            return (List<String>) value;
        } else {
            List<String> valueList = new ArrayList<>(8);
            valueList.add(String.valueOf(value));
            return valueList;
        }
    }
}
