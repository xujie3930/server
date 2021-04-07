package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.mapper.OperationMapper;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class OperationServiceImpl extends ServiceImpl<OperationMapper, Operation> implements IOperationService {

    @Resource
    private OperationMapper operationMapper;

    @Override
    public int save(OperationDTO dto) {
        Operation domain = new Operation();
        BeanUtils.copyProperties(dto,domain);
        return operationMapper.insert(domain);
    }

    @Override
    public int update(Operation dto) {
        return operationMapper.updateById(dto);
    }

    @Override
    public List<Operation> listPage(OperationDTO dto) {
        LambdaQueryWrapper<Operation> where = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq(Operation::getOperationType,dto.getOperationType());
        }
        if(StringUtils.isNotEmpty(dto.getOrderType())) {
            where.eq(Operation::getOrderType,dto.getOrderType());
        }
        if(StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            where.eq(Operation::getWarehouseCode,dto.getWarehouseCode());
        }
        return operationMapper.selectList(where);
    }

    @Override
    public Operation details(int id) {
        return operationMapper.selectById(id);
    }


}
