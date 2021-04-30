package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.InventoryInspectionDetails;
import com.szmsd.inventory.mapper.InventoryInspectionDetailsMapper;
import com.szmsd.inventory.service.IInventoryInspectionDetailsService;
import org.springframework.stereotype.Service;

@Service
public class InventoryInspectionDetailsServiceImpl extends ServiceImpl<InventoryInspectionDetailsMapper, InventoryInspectionDetails> implements IInventoryInspectionDetailsService {
}
