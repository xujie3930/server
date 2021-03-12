package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;

public interface IInventoryService extends IService<Inventory> {

    void inbound(InboundInventoryDTO inboundInventoryDTO);

}

