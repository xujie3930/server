package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.chargerules.domain.ChaOperation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.dto.OperationQueryDTO;
import com.szmsd.chargerules.vo.ChaOperationListVO;
import com.szmsd.chargerules.vo.ChaOperationVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
public interface ChaOperationMapper extends BaseMapper<ChaOperation> {

    List<ChaOperationListVO> queryOperationList(@Param(Constants.WRAPPER) LambdaQueryWrapper<ChaOperation> queryWrapper);

    List<ChaOperationVO> queryOperationDetailByRule(OperationQueryDTO queryDTO);
}
