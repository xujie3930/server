package com.szmsd.http.service;

import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.http.vo.ResponseVO2;

public interface IInventoryService {

    ResponseVO2<InventoryInfo> listing(String warehouseCode, String sku);

}
