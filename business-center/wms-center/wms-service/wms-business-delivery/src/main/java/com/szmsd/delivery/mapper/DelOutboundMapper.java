package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBatchUpdateTrackingNoDto;
import com.szmsd.delivery.dto.DelOutboundExportListDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.delivery.vo.DelOutboundListVO;
import com.szmsd.finance.dto.QueryChargeDto;
import com.szmsd.finance.vo.QueryChargeVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出库单 Mapper 接口
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
public interface DelOutboundMapper extends BaseMapper<DelOutbound> {

    /**
     * 出库管理 - 分页
     *
     * @param queryWrapper queryWrapper
     * @return DelOutboundListVO
     */
    @DataScope("o.seller_code")
    List<DelOutboundListVO> pageList(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundListQueryDto> queryWrapper);

    /**
     * 按条件查询出库单及详情列表
     *
     * @param queryWrapper queryWrapper
     * @return DelOutboundListVO
     */
    List<DelOutboundDetailListVO> getDelOutboundAndDetailsList(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundListQueryDto> queryWrapper);

    /**
     * 按条件查询出库单
     *
     * @param query query
     * @return list
     */
    @DataScope("t.custom_code")
    List<QueryChargeVO> selectDelOutboundList(QueryChargeDto query);

    /**
     * 出库管理 - 导出分页
     *
     * @param queryWrapper queryWrapper
     * @return List<DelOutboundExportListDto>
     */
    List<DelOutboundExportListDto> exportList(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundListQueryDto> queryWrapper);

    int updateTrackingNo(DelOutboundBatchUpdateTrackingNoDto dto);
}
