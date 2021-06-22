package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.InventoryWarningQueryDTO;
import com.szmsd.inventory.domain.dto.InventoryWarningSendEmailDTO;

import java.util.List;

public interface IInventoryWarningService extends IService<InventoryWarning> {

    void create(InventoryWarning inventoryWarning);

    void createAndSendEmail(String email, List<InventoryWarning> inventoryWarningList);

    List<InventoryWarning> selectList(InventoryWarningQueryDTO queryDTO);

    void sendEmail(InventoryWarningSendEmailDTO sendEmailDTO);
}
