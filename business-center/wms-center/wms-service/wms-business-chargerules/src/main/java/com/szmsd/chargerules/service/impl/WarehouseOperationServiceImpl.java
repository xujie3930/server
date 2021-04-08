package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.enums.ErrorMessageEnum;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.exception.web.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
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
        LambdaQueryWrapper<WarehouseOperation> where = Wrappers.lambdaQuery();
        if(StringUtils.isNotBlank(dto.getWarehouseCode())) {
            where.eq(WarehouseOperation::getWarehouseCode,dto.getWarehouseCode());
        }
        return warehouseOperationMapper.selectList(where);
    }

    @Override
    public BigDecimal charge(int days, BigDecimal cbm, String warehouseCode, List<WarehouseOperation> dto) {

        Optional<WarehouseOperation> optional = dto.stream().filter(value -> value.getWarehouseCode().equals(warehouseCode)
                && days > value.getChargeDays()).max(Comparator.comparing(WarehouseOperation::getChargeDays));
        if (optional.isPresent()) {
            WarehouseOperation warehouseOperation = optional.get();
            return cbm.multiply(warehouseOperation.getPrice()); //体积乘以价格
        }
        log.error("charge() warehouseCode: {}, days: {}",warehouseCode,days);
        throw new BaseException(ErrorMessageEnum.WAREHOUSE_PRICE_NOT_FOUND.getMessage());
    }

    @Override
    public WarehouseOperation details(int id) {
        return warehouseOperationMapper.selectById(id);
    }

}
