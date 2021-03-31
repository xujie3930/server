package com.szmsd.inventory.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.http.api.feign.HtpInventoryCheckFeignService;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.vo.ResponseVO;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;
import com.szmsd.inventory.enums.InventoryStatusEnum;
import com.szmsd.inventory.mapper.InventoryCheckMapper;
import com.szmsd.inventory.service.IInventoryCheckService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class IInventoryCheckServiceImpl implements IInventoryCheckService {

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;

    @Resource
    private HtpInventoryCheckFeignService htpInventoryCheckFeignService;

    @Override
    public int add(InventoryCheckDTO inventoryCheckDTO) {
        InventoryCheck inventoryCheck = new InventoryCheck();
        BeanUtils.copyProperties(inventoryCheckDTO, inventoryCheck);
        return inventoryCheckMapper.insert(inventoryCheck);
    }

    @Override
    public List<InventoryCheck> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO) {
        return inventoryCheckMapper.findList(inventoryCheckQueryDTO);
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
