package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        QueryWrapper<Operation> where = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq("operation_type",dto.getOperationType());
        }
        return operationMapper.selectList(where);
    }


}
