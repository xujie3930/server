package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.chargerules.domain.ChaOperationDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.vo.ChaOperationDetailsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 费用规则明细表 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
public interface ChaOperationDetailsMapper extends BaseMapper<ChaOperationDetails> {

    List<ChaOperationDetailsVO> queryDetailByOpeId(Long operationId);
}
