package com.szmsd.bas.api.service;

import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface BasWarehouseClientService {

    R saveOrUpdate(AddWarehouseRequest addWarehouseRequest);

    BasWarehouse queryByWarehouseCode(String warehouseCode);

    List<BasWarehouse> queryByWarehouseCodes(List<String> warehouseCodes);
}
