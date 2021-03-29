package com.szmsd.bas.plugin;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.common.plugin.interfaces.CacheContext;
import com.szmsd.common.plugin.interfaces.CommonPlugin;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-29 14:39
 */
@Component
public class BasWarehouseCommonPlugin implements CommonPlugin {
    public static final String SUPPORTS = "BasWarehouse";

    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Override
    public String supports() {
        return SUPPORTS;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public Map<Object, Object> handlerValue(List<Object> warehouseCodes, DefaultCommonParameter cp, CacheContext cacheContext) {
        List<BasWarehouse> basWarehouseList = basWarehouseClientService.queryByWarehouseCodes((List<String>) (List<?>) warehouseCodes);
        if (CollectionUtils.isEmpty(basWarehouseList)) {
            return Collections.emptyMap();
        }
        Map<Object, Object> map = new HashMap<>();
        for (BasWarehouse warehouse : basWarehouseList) {
            map.put(warehouse.getWarehouseCode(), warehouse.getWarehouseNameCn());
        }
        return map;
    }
}
