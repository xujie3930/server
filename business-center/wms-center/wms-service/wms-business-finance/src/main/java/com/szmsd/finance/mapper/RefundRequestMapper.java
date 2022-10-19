package com.szmsd.finance.mapper;

import com.szmsd.finance.domain.FssRefundRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.finance.dto.RefundRequestQueryDTO;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.finance.vo.RefundRequestVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 退费记录表 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */
public interface RefundRequestMapper extends BaseMapper<FssRefundRequest> {

    List<RefundRequestListVO> selectRequestList(RefundRequestQueryDTO queryDTO);

    List<Map> selectOutbounds(@Param("orderNo") String orderNo);
}
