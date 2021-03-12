package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.ReceivingRequest;

public interface IInventoryService extends IService<Inventory> {

    void inbound(ReceivingRequest receivingRequest);

}

