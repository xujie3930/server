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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class WarehouseOperationServiceImpl extends ServiceImpl<WarehouseOperationMapper, WarehouseOperation> implements IWarehouseOperationService {

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private IWarehouseOperationDetailsService warehouseOperationDetailsService;

    private final String REGEX = "\\d*D\\d*";

    @Transactional
    @Override
    public int save(WarehouseOperationDTO dto) {
        checkWarehouse(dto);
        WarehouseOperation domain = BeanMapperUtil.map(dto, WarehouseOperation.class);
        AssertUtil.notEmpty(dto.getDetails(), "详情必填");
        warehouseOperationMapper.insert(domain);
        long count = dto.getDetails().stream().map(value -> value.setWarehouseOperationId(domain.getId())).filter(value -> !Pattern.matches(REGEX, value.getChargeDays())).count();
        if (count > 0) throw new CommonException("999", "计费天数格式错误");
        List<WarehouseOperationVo> warehouseOperationDb = this.listPage(dto);
        if(isIntersection(dto.getDetails(), warehouseOperationDb.get(0).getDetails())) throw new CommonException("999","仓库+区间存在重合");
        warehouseOperationDetailsService.saveBatch(dto.getDetails());
        return 1;
    }

    /**
     * 检验仓库是否存在 ：仓库唯一
     * @param dto dto
     */
    private void checkWarehouse(WarehouseOperationDTO dto) {
        AssertUtil.notNull(dto.getWarehouseCode(), "仓库必填");
        LambdaQueryWrapper<WarehouseOperation> query = Wrappers.lambdaQuery();
        query.eq(WarehouseOperation::getWarehouseCode, dto.getWarehouseCode());
        int count = this.count(query);
        if(count > 0) throw new CommonException("999","仓库已存在");
    }

    /**
     * 检验天数是否合法
     * @param dto list
     * @param db db
     * @return true：重合/不合法 false：不重合/合法
     */
    public boolean isIntersection(List<WarehouseOperationDetails> dto, List<WarehouseOperationDetails> db) {
        if(dto.size() == 1 && db.size() == 0) return false;
        List<WarehouseOperationDetails> list = Stream.of(dto, db).flatMap(Collection::stream).collect(Collectors.toList());
        for (int i = 0; i < list.size(); i++) {
            String[] ds = list.get(i).getChargeDays().split("D");
            int start = Integer.parseInt(ds[0]);
            int end = Integer.parseInt(ds[1]);
            for (int j = i + 1; j < list.size(); j++) {
                String[] ds2 = list.get(j).getChargeDays().split("D");
                int start2 = Integer.parseInt(ds2[0]);
                int end2 = Integer.parseInt(ds2[1]);
                if (Math.max(start, start2) < Math.min(end, end2)) {
                    log.error("区间存在重叠交叉！start:{}, end:{}, start2:{}, end2:{}",start,end,start2,end2);
                    return true;
                }
            }
        }
        return false;

    }

    @Transactional
    @Override
    public int update(WarehouseOperationDTO dto) {
        long count = dto.getDetails().stream().filter(value -> !Pattern.matches(REGEX, value.getChargeDays())).count();
        if (count > 0) throw new CommonException("999", "计费天数格式错误");
        WarehouseOperation map = BeanMapperUtil.map(dto, WarehouseOperation.class);
        this.updateDetails(dto);
        return warehouseOperationMapper.updateById(map);
    }

    private void updateDetails(WarehouseOperationDTO dto) {
        LambdaQueryWrapper<WarehouseOperationDetails> query = Wrappers.lambdaQuery();
        query.eq(WarehouseOperationDetails::getWarehouseOperationId, dto.getId());
        warehouseOperationDetailsService.remove(query);
        if(isIntersection(dto.getDetails(),new ArrayList<>())) throw new CommonException("999","仓库+区间存在重合");
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
