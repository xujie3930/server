package com.szmsd.inventory.service.impl;

import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import com.szmsd.inventory.service.IInventoryCheckOpenService;
import org.springframework.stereotype.Service;

@Service
public class IInventoryCheckOpenServiceImpl implements IInventoryCheckOpenService {

    @Override
    public int adjust(AdjustRequestDto adjustRequestDto) {
        return 0;
    }

    @Override
    public int counting(CountingRequestDto countingRequestDto) {
        return 0;
    }
}
