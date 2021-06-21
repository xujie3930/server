package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.InventoryWarning;

import java.util.List;

public interface IInventoryWarningService extends IService<InventoryWarning> {

    void create(InventoryWarning inventoryWarning);

    void createAndSendEmail(String email, List<InventoryWarning> inventoryWarningList);

}
