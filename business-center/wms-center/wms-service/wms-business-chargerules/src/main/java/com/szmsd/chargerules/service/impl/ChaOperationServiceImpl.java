package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.vo.BasSellerInfoVO;
import com.szmsd.chargerules.domain.ChaOperation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.mapper.ChaOperationMapper;
import com.szmsd.chargerules.service.IBaseInfoService;
import com.szmsd.chargerules.service.IChaOperationDetailsService;
import com.szmsd.chargerules.service.IChaOperationService;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
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
@Slf4j
@Service
public class ChaOperationServiceImpl extends ServiceImpl<ChaOperationMapper, ChaOperation> implements IChaOperationService {

    @Resource
    private IChaOperationDetailsService iChaOperationDetailsService;
    @Resource
    private BasSellerFeignService basSellerFeignService;

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
        // max(A.left,B.left)<=min(A.right,B.right) 重复
        // 判断生效时间是否冲突 既相交
        LocalDateTime effectiveTime = dto.getEffectiveTime();
        LocalDateTime expirationTime = dto.getExpirationTime();

        if (CollectionUtils.isNotEmpty(operations)) {
            boolean present = operations.parallelStream()
                    .anyMatch(x -> {
                        LocalDateTime max = effectiveTime.compareTo(x.getEffectiveTime()) >= 0 ? effectiveTime : x.getEffectiveTime();
                        LocalDateTime min = expirationTime.compareTo(x.getExpirationTime()) >= 0 ? expirationTime : x.getExpirationTime();
                        return max.compareTo(min) <= 0;
                    });
            AssertUtil.isTrue(!present, "已存在相同配置的费用规则");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        LambdaQueryWrapper<ChaOperation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper
                .le(queryDTO.getEffectiveTime() != null && queryDTO.getExpirationTime() != null, ChaOperation::getExpirationTime, queryDTO.getExpirationTime())
                .ge(queryDTO.getEffectiveTime() != null && queryDTO.getExpirationTime() != null, ChaOperation::getEffectiveTime, queryDTO.getEffectiveTime())
                .eq(StringUtils.isNotBlank(queryDTO.getCurrencyCode()), ChaOperation::getCurrencyCode, queryDTO.getCurrencyCode())
                .eq(StringUtils.isNotBlank(queryDTO.getOperationType()), ChaOperation::getOperationType, queryDTO.getOperationType())
                .eq(StringUtils.isNotBlank(queryDTO.getOrderType()), ChaOperation::getOrderType, queryDTO.getOrderType())
                .eq(StringUtils.isNotBlank(queryDTO.getWarehouseCode()), ChaOperation::getWarehouseCode, queryDTO.getWarehouseCode())
                .eq(StringUtils.isNotBlank(queryDTO.getCusTypeCode()), ChaOperation::getCusTypeCode, queryDTO.getCusTypeCode())
                .apply(StringUtils.isNotBlank(queryDTO.getCusCodeList()), "CONCAT(',',cus_code_list,',') REGEXP(SELECT CONCAT(',',REPLACE({0}, ',', ',|,'),','))", queryDTO.getCusCodeList())
                .orderByDesc(ChaOperation::getId);
        return baseMapper.queryOperationList(queryWrapper);
    }

    @Override
    public Integer deleteById(Integer id) {
        int i = baseMapper.deleteById(id);
        iChaOperationDetailsService.deleteByOperationId(id);
        return i;
    }

    /**
     * 查询用户的匹配规则 查询不校验
     * 用户类型.客户id二选一
     *
     * @param queryDTO 查询条件
     * @return 唯一生效的结果
     */
    @Override
    public ChaOperationVO queryOperationDetailByRule(OperationQueryDTO queryDTO) {
        log.info("查询用户规则信息：{}", queryDTO);
        String cusTypeCode = queryDTO.getCusTypeCode();
        String cusCodeList = queryDTO.getCusCodeList();
        boolean queryByCusCode = StringUtils.isNotBlank(cusCodeList);

        if (queryByCusCode) {
            queryDTO.setCusTypeCode(null);
        }
        //（生效+仓库+操作类型+订单类型）先查到对应的规则，是先按个人匹配，个人没有就按照用户类型 然后再折扣信息列表里面匹配对应的范围区间
        List<ChaOperationListVO> chaOperationList = this.queryOperationList(queryDTO);

        if (CollectionUtils.isEmpty(chaOperationList)) {
            //查询用户的类型
            if (queryByCusCode) {
                R<BasSellerInfoVO> info = basSellerFeignService.getInfoBySellerCode(cusCodeList);
                BasSellerInfoVO userInfo = R.getDataAndException(info);
                String discountUserType = userInfo.getDiscountUserType();
                if (StringUtils.isBlank(discountUserType)) return null;
                cusTypeCode = discountUserType;
            }
            queryDTO.setCusCodeList(null);
            queryDTO.setCusTypeCode(cusTypeCode);
            chaOperationList = this.queryOperationList(queryDTO);
        }

        if (CollectionUtils.isEmpty(chaOperationList)) return null;
        ChaOperationVO chaOperationVO = new ChaOperationVO();

        ChaOperationListVO chaOperationListVO = chaOperationList.get(0);
        BeanUtils.copyProperties(chaOperationListVO, chaOperationVO);
        Long id = chaOperationVO.getId();
        List<ChaOperationDetailsVO> chaOperationDetailsVOList = iChaOperationDetailsService.queryDetailByOpeId(id);
        chaOperationVO.setChaOperationDetailList(chaOperationDetailsVOList);
        return chaOperationVO;
    }

}

