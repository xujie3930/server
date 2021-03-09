package com.szmsd.bas.api.service;

import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;

public interface BasWarehouseClientService {

    R saveOrUpdate(AddWarehouseRequest addWarehouseRequest);
}
