package com.szmsd.inventory.service.impl;

import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.inventory.domain.InventoryCounting;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import com.szmsd.inventory.mapper.IInventoryCheckOpenMapper;
import com.szmsd.inventory.service.IInventoryCheckOpenService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IInventoryCheckOpenServiceImpl implements IInventoryCheckOpenService {

    @Resource
    private IInventoryCheckOpenMapper iInventoryCheckOpenMapper;

    @Override
    public int adjust(AdjustRequestDto adjustRequestDto) {
        return 0;
    }

    @Override
    public int counting(CountingRequestDto countingRequestDto) {
        InventoryCounting inventoryCounting = new InventoryCounting();
        BeanUtils.copyProperties(countingRequestDto,inventoryCounting);
        return iInventoryCheckOpenMapper.insert(inventoryCounting);
    }
}
