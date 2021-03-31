package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;

public interface IInventoryCheckOpenService {

    /**
     * 校准仓库库存
     * @param adjustRequestDto adjustRequestDto
     * @return result
     */
    int adjust(AdjustRequestDto adjustRequestDto);

    /**
     * 接收申请盘点结果
     * @param countingRequestDto countingRequestDto
     * @return result
     */
    int counting(CountingRequestDto countingRequestDto);

}
