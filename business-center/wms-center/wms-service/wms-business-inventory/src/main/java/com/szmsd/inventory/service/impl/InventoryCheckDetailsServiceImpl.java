package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.InventoryCheckDetails;
import com.szmsd.inventory.mapper.InventoryCheckDetailsMapper;
import com.szmsd.inventory.service.IInventoryCheckDetailsService;
import org.springframework.stereotype.Service;

@Service
public class InventoryCheckDetailsServiceImpl extends ServiceImpl<InventoryCheckDetailsMapper, InventoryCheckDetails> implements IInventoryCheckDetailsService {

}
