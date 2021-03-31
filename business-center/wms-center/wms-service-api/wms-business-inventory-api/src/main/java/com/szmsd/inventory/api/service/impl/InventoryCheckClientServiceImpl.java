package com.szmsd.inventory.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryCheckFeignService;
import com.szmsd.inventory.api.service.IInventoryCheckClientService;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class InventoryCheckClientServiceImpl implements IInventoryCheckClientService {

    @Resource
    private InventoryCheckFeignService inventoryCheckFeignService;

    @Override
    public Integer adjust(AdjustRequestDto adjustRequestDto) {
        return R.getDataAndException(inventoryCheckFeignService.adjust(adjustRequestDto));
    }

    @Override
    public Integer counting(CountingRequestDto countingRequestDto) {
        return R.getDataAndException(inventoryCheckFeignService.counting(countingRequestDto));
    }
}
