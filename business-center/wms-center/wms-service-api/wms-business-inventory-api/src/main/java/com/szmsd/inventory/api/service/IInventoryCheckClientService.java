package com.szmsd.inventory.api.service;

import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;

public interface IInventoryCheckClientService {

    Integer adjust(AdjustRequestDto adjustRequestDto);

    Integer counting(CountingRequestDto countingRequestDto);
}
