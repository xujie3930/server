package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.mapper.InventoryRecordMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import org.springframework.stereotype.Service;

@Service
public class InventoryRecordServiceImpl extends ServiceImpl<InventoryRecordMapper, InventoryRecord> implements IInventoryRecordService {
}

