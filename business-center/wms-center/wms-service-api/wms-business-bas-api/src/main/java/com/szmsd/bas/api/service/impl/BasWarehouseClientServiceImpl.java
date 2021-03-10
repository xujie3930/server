package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.api.service.BasWarehouseClientService;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BasWarehouseClientServiceImpl implements BasWarehouseClientService {

    @Resource
    private BasWarehouseFeignService basWarehouseFeignService;

    /**
     * 创建/更新仓库
     * @param addWarehouseRequest
     */
    @Override
    public R saveOrUpdate(AddWarehouseRequest addWarehouseRequest) {
        return basWarehouseFeignService.saveOrUpdate(addWarehouseRequest);
    }
}
