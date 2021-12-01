package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.ChaOperation;
import com.szmsd.chargerules.domain.ChaOperationDetails;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.ChaOperationDetailsDTO;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.chargerules.mapper.ChaOperationMapper;
import com.szmsd.chargerules.service.IChaOperationDetailsService;
import com.szmsd.chargerules.service.IChaOperationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
@Service
public class ChaOperationServiceImpl extends ServiceImpl<ChaOperationMapper, ChaOperation> implements IChaOperationService {

    @Resource
    private IChaOperationDetailsService iChaOperationDetailsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int save(OperationDTO dto) {
        // 校验区间是否冲突
        dto.verifyData();
        this.validDateBefore(dto);
        ChaOperation operation = new ChaOperation();
        BeanUtils.copyProperties(dto, operation);
        int insertResult = baseMapper.insert(operation);
        dto.setId(operation.getId());
        // 插入明细
        iChaOperationDetailsService.saveOrUpdateDetailList(dto);
        return insertResult;
    }

    private void validDateBefore(OperationDTO dto) {
        String operationType = dto.getOperationType();
        String warehouseCode = dto.getWarehouseCode();
        String currencyCode = dto.getCurrencyCode();
        String orderType = dto.getOrderType();
        String cusTypeCode = dto.getCusTypeCode();
        Long id = dto.getId();
        // 同一个 订单类型+仓库+币别+客户名称+操作类型+币别 唯一 且生效时间不冲突 //TODO 一个用户不能在多个规则里面
        List<ChaOperation> operations = baseMapper.selectList(Wrappers.<ChaOperation>lambdaQuery()
                .eq(ChaOperation::getOrderType, orderType)
                .eq(ChaOperation::getOperationType, operationType)
                .eq(ChaOperation::getWarehouseCode, warehouseCode)
                .eq(ChaOperation::getCusTypeCode, cusTypeCode)
                .eq(ChaOperation::getCurrencyCode, currencyCode)
                .select(ChaOperation::getId, ChaOperation::getCurrencyCode, ChaOperation::getEffectiveTime, ChaOperation::getExpirationTime));
        if (Objects.nonNull(id)) {
            operations = operations.stream().filter(x -> x.getId().compareTo(id) != 0).collect(Collectors.toList());
        }
        //A.right< B.left|| A.left> B.right
        // max(A.left,B.left)<=min(A.right,B.right) 重复
        // 判断生效时间是否冲突 既相交
        LocalDateTime effectiveTime = dto.getEffectiveTime();
        LocalDateTime expirationTime = dto.getExpirationTime();
        boolean present = false;
        if (CollectionUtils.isNotEmpty(operations)) {
            present = operations.parallelStream()
                    .anyMatch(x -> {
                        LocalDateTime max = effectiveTime.compareTo(x.getEffectiveTime()) >= 0 ? effectiveTime : x.getEffectiveTime();
                        LocalDateTime min = expirationTime.compareTo(x.getExpirationTime()) >= 0 ? expirationTime : x.getExpirationTime();
                        return max.compareTo(min) >= 0;
                    });
        }
        AssertUtil.isTrue(!present, "已存在相同配置的费用规则");
    }

    @Override
    public int update(OperationDTO dto) {
        dto.verifyData();
        this.validDateBefore(dto);
        ChaOperation operation = new ChaOperation();
        BeanUtils.copyProperties(dto, operation);
        int insertResult = baseMapper.updateById(operation);
        dto.setId(operation.getId());
        // 插入明细
        iChaOperationDetailsService.saveOrUpdateDetailList(dto);
        return insertResult;
    }

    @Override
    public ChaOperationVO queryDetails(Long id) {
        ChaOperationVO chaOperationVO = new ChaOperationVO();
        ChaOperation chaOperation = baseMapper.selectById(id);
        if (Objects.nonNull(chaOperation)) {
            BeanUtils.copyProperties(chaOperation, chaOperationVO);
            List<ChaOperationDetailsVO> chaOperationDetailsVOList = iChaOperationDetailsService.queryDetailByOpeId(id);
            chaOperationVO.setChaOperationDetailList(chaOperationDetailsVOList);
        }
        return chaOperationVO;
    }

    @Override
    public List<ChaOperationListVO> queryOperationList(OperationQueryDTO queryDTO) {
        LambdaQueryWrapper<Object> queryWrapper = Wrappers.lambdaQuery();
        return baseMapper.queryOperationList(queryWrapper);
    }

    @Override
    public Integer deleteById(Integer id) {
        int i = baseMapper.deleteById(id);
        iChaOperationDetailsService.deleteByOperationId(id);
        return i;
    }
}

