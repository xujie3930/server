package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryService;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

}