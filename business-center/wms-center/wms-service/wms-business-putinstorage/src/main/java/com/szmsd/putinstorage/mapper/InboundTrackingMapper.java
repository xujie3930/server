package com.szmsd.putinstorage.mapper;

import com.szmsd.putinstorage.domain.InboundTracking;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.putinstorage.domain.vo.InboundTrackingExportVO;
import com.szmsd.putinstorage.domain.vo.InboundTrackingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 入库物流到货记录 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-09-06
 */
public interface InboundTrackingMapper extends BaseMapper<InboundTracking> {

    List<InboundTrackingExportVO> selectInboundTrackingList(@Param("list") List<String> orderNoList);
}
