package com.szmsd.doc.validator;

import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.domain.BasWarehouse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component(value = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN)
public class WarDictionaryPlugin implements DictionaryPlugin {

    @Autowired
    private BasWarehouseClientService basWarehouseClientService;

    @Override
    public boolean valid(Object value, String param) {
        // value是仓库编码
        BasWarehouse basWarehouse = this.basWarehouseClientService.queryByWarehouseCode(String.valueOf(value));
        return Objects.nonNull(basWarehouse);
    }
}
