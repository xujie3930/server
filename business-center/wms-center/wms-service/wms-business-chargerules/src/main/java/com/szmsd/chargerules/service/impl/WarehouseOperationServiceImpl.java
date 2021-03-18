package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msd.chargerules.domain.WarehouseOperation;
import com.msd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.exception.web.BaseException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
public class WarehouseOperationServiceImpl extends ServiceImpl<WarehouseOperationMapper, WarehouseOperation> implements IWarehouseOperationService {

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Override
    public int save(WarehouseOperationDTO dto) {
        WarehouseOperation domain = new WarehouseOperation();
        BeanUtils.copyProperties(dto, domain);
        return warehouseOperationMapper.insert(domain);
    }

    @Override
    public int update(WarehouseOperation dto) {
        return warehouseOperationMapper.updateById(dto);
    }

    @Override
    public List<WarehouseOperation> listPage(WarehouseOperationDTO dto) {
        QueryWrapper<WarehouseOperation> where = new QueryWrapper<>();
        return warehouseOperationMapper.selectList(where);
    }

    @Override
    public BigDecimal charge(int days, BigDecimal cbm, String warehouseCode, List<WarehouseOperation> dto) {

        Optional<WarehouseOperation> optional = dto.stream().filter(value -> days > value.getChargeDays()).
                max(Comparator.comparing(WarehouseOperation::getChargeDays));
        if (optional.isPresent()) {
            WarehouseOperation warehouseOperation = optional.get();
            return cbm.multiply(warehouseOperation.getPrice()); //体积乘以价格
        }

        throw new BaseException("仓储计费失败");
    }

}
