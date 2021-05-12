package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import com.szmsd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IWarehouseOperationDetailsService;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.chargerules.vo.WarehouseOperationVo;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class WarehouseOperationServiceImpl extends ServiceImpl<WarehouseOperationMapper, WarehouseOperation> implements IWarehouseOperationService {

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private IWarehouseOperationDetailsService warehouseOperationDetailsService;

    @Transactional
    @Override
    public int save(WarehouseOperationDTO dto) {
        WarehouseOperation domain = new WarehouseOperation();
        BeanUtils.copyProperties(dto, domain);
        AssertUtil.notEmpty(dto.getDetails(), "详情必填");
        warehouseOperationMapper.insert(domain);
        long count = dto.getDetails().stream().map(value -> value.setWarehouseOperationId(domain.getId())).filter(value -> !value.getChargeDays().contains("D")).count();
        if (count > 0) throw new CommonException("999", "计费天数必须包含D");
        warehouseOperationDetailsService.saveBatch(dto.getDetails());
        return 1;
    }

    @Transactional
    @Override
    public int update(WarehouseOperationDTO dto) {
        WarehouseOperation map = BeanMapperUtil.map(dto, WarehouseOperation.class);
        this.updateDetails(dto);
        return warehouseOperationMapper.updateById(map);
    }

    private void updateDetails(WarehouseOperationDTO dto) {
        LambdaQueryWrapper<WarehouseOperationDetails> query = Wrappers.lambdaQuery();
        query.eq(WarehouseOperationDetails::getWarehouseOperationId, dto.getId());
        warehouseOperationDetailsService.remove(query);
        warehouseOperationDetailsService.saveBatch(dto.getDetails());
    }

    @Override
    public List<WarehouseOperationVo> listPage(WarehouseOperationDTO dto) {
        return warehouseOperationMapper.listPage(dto);
    }

    private boolean isInTheInterval(long current, long min, long max) {
        return Math.max(min, current) == Math.min(current, max);
    }

    @Override
    public BigDecimal charge(int days, BigDecimal cbm, String warehouseCode, WarehouseOperationVo dto) {
        List<WarehouseOperationDetails> details = dto.getDetails();
        for (WarehouseOperationDetails detail : details) {
            String chargeDays = detail.getChargeDays();
            String[] ds = chargeDays.split("D");
            int start = Integer.parseInt(ds[0]);
            int end = Integer.parseInt(ds[1]);
            if (isInTheInterval(days, start, end)) {
                return cbm.multiply(detail.getPrice());
            }
        }
        log.error("未找到该储存仓租的计费配置 days：{},warehouseCode {}", days, warehouseCode);
        return BigDecimal.ZERO;
    }

    @Override
    public WarehouseOperationVo details(int id) {
        return warehouseOperationMapper.selectDetailsById(id);
    }

}
