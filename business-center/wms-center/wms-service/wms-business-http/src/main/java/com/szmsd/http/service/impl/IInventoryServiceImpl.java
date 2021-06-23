package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.service.IInventoryService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.http.vo.ResponseVO2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IInventoryServiceImpl extends WmsRequest implements IInventoryService {

    public IInventoryServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO2<InventoryInfo> listing(String warehouseCode, String sku) {
        Map<String, String> params = new HashMap<>();
        params.put("warehouseCode", warehouseCode);
        params.put("sku", sku);
        return JSON.parseObject(httpGet(warehouseCode, "inventory.listing", params), new TypeReference<ResponseVO2<InventoryInfo>>() {
        });
    }
}
