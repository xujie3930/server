package com.szmsd.bas.api.service;

import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

public interface BasWarehouseClientService {

    R saveOrUpdate(AddWarehouseRequest addWarehouseRequest);

    BasWarehouse queryByWarehouseCode(String warehouseCode);
}
