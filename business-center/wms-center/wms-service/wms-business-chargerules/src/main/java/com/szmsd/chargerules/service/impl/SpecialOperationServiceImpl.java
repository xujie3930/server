package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.msd.chargerules.domain.SpecialOperation;
import com.msd.chargerules.dto.SpecialOperationDTO;
import com.szmsd.chargerules.mapper.SpecialOperationMapper;
import com.szmsd.chargerules.service.ISpecialOperationService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SpecialOperationServiceImpl extends ServiceImpl<SpecialOperationMapper, SpecialOperation> implements ISpecialOperationService {

    @Resource
    private SpecialOperationMapper specialOperationMapper;

    @Override
    public int save(SpecialOperationDTO dto) {
        SpecialOperation domain = new SpecialOperation();
        BeanUtils.copyProperties(dto,domain);
        int result = specialOperationMapper.insert(domain);
        if(result > 0) {
            //调用WMS接口
        }
        return result;
    }

    @Override
    public int update(SpecialOperation dto) {
        return specialOperationMapper.updateById(dto);
    }

    @Override
    public List<SpecialOperation> listPage(SpecialOperationDTO dto) {
        QueryWrapper<SpecialOperation> where = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(dto.getOperationType())) {
            where.eq("operation_type",dto.getOperationType());
        }
        return specialOperationMapper.selectList(where);
    }


}
