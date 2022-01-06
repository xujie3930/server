package com.szmsd.chargerules.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.chargerules.domain.ChaOperationDetails;
import com.szmsd.chargerules.dto.ChaOperationDetailsDTO;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.mapper.ChaOperationDetailsMapper;
import com.szmsd.chargerules.service.IChaOperationDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 费用规则明细表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
@Service
public class ChaOperationDetailsServiceImpl extends ServiceImpl<ChaOperationDetailsMapper, ChaOperationDetails> implements IChaOperationDetailsService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateDetailList(OperationDTO dto) {
        Long operationId = dto.getId();
        List<ChaOperationDetailsDTO> chaOperationDetailList = dto.getChaOperationDetailList();
        if (CollectionUtils.isEmpty(chaOperationDetailList)) {
            baseMapper.delete(Wrappers.<ChaOperationDetails>lambdaUpdate().eq(ChaOperationDetails::getOperationId, operationId));
        } else {
            List<Long> deleteIdList = chaOperationDetailList.stream().map(ChaOperationDetailsDTO::getId).collect(Collectors.toList());
            baseMapper.delete(Wrappers.<ChaOperationDetails>lambdaUpdate().eq(ChaOperationDetails::getOperationId, operationId).notIn(CollectionUtils.isNotEmpty(deleteIdList),ChaOperationDetails::getId, deleteIdList));
            List<ChaOperationDetails> detailsList = chaOperationDetailList.stream().map(x -> {
                ChaOperationDetails chaOperationDetails = new ChaOperationDetails();
                BeanUtils.copyProperties(x, chaOperationDetails);
                chaOperationDetails.setOperationId(operationId);
                return chaOperationDetails;
            }).collect(Collectors.toList());
            this.saveOrUpdateBatch(detailsList);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteByOperationId(Integer id) {
        return baseMapper.delete(Wrappers.<ChaOperationDetails>lambdaUpdate().eq(ChaOperationDetails::getOperationId, id));
    }

    @Override
    public List<ChaOperationDetailsVO> queryDetailByOpeId(Long id) {
        return baseMapper.queryDetailByOpeId(id);
    }

    @Override
    public List<ChaOperationDetailsVO> queryDetailByOpeIdList(List<Long> idList) {
        return baseMapper.queryDetailByOpeIdList(idList);
    }
}

