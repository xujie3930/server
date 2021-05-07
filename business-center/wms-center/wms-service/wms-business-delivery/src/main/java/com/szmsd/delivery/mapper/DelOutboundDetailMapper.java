package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.dto.DelOutboundExportItemListDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出库单明细 Mapper 接口
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
public interface DelOutboundDetailMapper extends BaseMapper<DelOutboundDetail> {

    /**
     * 出库管理 - 导出分页
     *
     * @param queryWrapper queryWrapper
     * @return List<DelOutboundExportItemListDto>
     */
    List<DelOutboundExportItemListDto> exportList(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundListQueryDto> queryWrapper);
}
