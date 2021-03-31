package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;
import com.szmsd.inventory.mapper.InventoryCheckMapper;
import com.szmsd.inventory.service.IInventoryCheckService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IInventoryCheckServiceImpl implements IInventoryCheckService {

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;

    @Override
    public int add(InventoryCheckDTO inventoryCheckDTO) {
        InventoryCheck inventoryCheck = new InventoryCheck();
        BeanUtils.copyProperties(inventoryCheckDTO,inventoryCheck);
        return inventoryCheckMapper.insert(inventoryCheck);
    }

    @Override
    public List<InventoryCheck> findList(InventoryCheckDTO inventoryCheckDTO) {
        LambdaQueryWrapper<InventoryCheck> query = Wrappers.lambdaQuery();
        return inventoryCheckMapper.selectList(query);
    }

    @Override
    public InventoryCheck details(int id) {
        return inventoryCheckMapper.selectById(id);
    }

    @Override
    public int update(InventoryCheck inventoryCheck) {
        return inventoryCheckMapper.updateById(inventoryCheck);
    }

}
