package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.domain.SpecialOperation;
import com.szmsd.chargerules.dto.SpecialOperationDTO;
import com.szmsd.chargerules.mapper.SpecialOperationMapper;
import com.szmsd.chargerules.service.ISpecialOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.http.api.feign.HtpBasFeignService;
import com.szmsd.http.dto.SpecialOperationRequest;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class SpecialOperationServiceImpl extends ServiceImpl<SpecialOperationMapper, SpecialOperation> implements ISpecialOperationService {

    @Resource
    private SpecialOperationMapper specialOperationMapper;

    @Resource
    private HtpBasFeignService htpBasFeignService;

    @Transactional
    @Override
    public int save(SpecialOperationDTO dto) {
        SpecialOperation domain = new SpecialOperation();
        BeanUtils.copyProperties(dto, domain);
        int result = specialOperationMapper.insert(domain);
        specialOperationType(domain, result);
        return result;
    }

    @Transactional
    @Override
    public int update(SpecialOperation dto) {
        int result = specialOperationMapper.updateById(dto);
        specialOperationType(dto, result);
        return result;
    }

    /**
     * 调用WMS接口传入特殊操作
     * @param specialOperation specialOperation
     * @param result result
     */
    private void specialOperationType(SpecialOperation specialOperation, int result) {
        if (result > 0) {
            SpecialOperationRequest specialOperationRequest = new SpecialOperationRequest();
            specialOperationRequest.setOperationType(String.valueOf(specialOperation.getId()));
            specialOperationRequest.setOperationTypeDesc(specialOperation.getOperationType());
            specialOperationRequest.setUnit(specialOperation.getUnit());
            specialOperationRequest.setWarehouseCode(specialOperation.getWarehouseCode());
            R<ResponseVO> response = htpBasFeignService.specialOperationType(specialOperationRequest);
            if (response.getCode() != 200) {
                log.error("specialOperationType() 传wms失败: {}",response.getMsg());
                throw new CommonException("999", "新增/修改特殊操作类型失败");
            }
        }
    }

    @Override
    public List<SpecialOperation> listPage(SpecialOperationDTO dto) {
        LambdaQueryWrapper<SpecialOperation> where = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(dto.getOperationType())) {
            where.like(SpecialOperation::getOperationType, dto.getOperationType());
        }
        if (StringUtils.isNotEmpty(dto.getWarehouseCode())) {
            where.eq(SpecialOperation::getWarehouseCode, dto.getWarehouseCode());
        }
        where.orderByDesc(SpecialOperation::getCreateTime);
        return specialOperationMapper.selectList(where);
    }

    @Override
    public SpecialOperation selectOne(BasSpecialOperation basSpecialOperation) {
        QueryWrapper<SpecialOperation> where = new QueryWrapper<>();
        if (StringUtils.isNotNull(basSpecialOperation.getOperationType())) {
            where.eq("operation_type", basSpecialOperation.getOperationType());
        }
        if (StringUtils.isNotNull(basSpecialOperation.getWarehouseCode())) {
            where.eq("warehouse_code", basSpecialOperation.getWarehouseCode());
        }
        return specialOperationMapper.selectOne(where);
    }

    @Override
    public SpecialOperation details(int id) {
        return specialOperationMapper.selectById(id);
    }

}
