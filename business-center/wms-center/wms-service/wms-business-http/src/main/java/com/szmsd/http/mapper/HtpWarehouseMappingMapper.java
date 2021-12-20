package com.szmsd.http.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.http.domain.HtpWarehouseMapping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.http.dto.mapping.HtpWarehouseMappingQueryDTO;
import com.szmsd.http.vo.mapping.HtpWarehouseMappingVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 仓库与仓库关联映射 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-12-13
 */
public interface HtpWarehouseMappingMapper extends BaseMapper<HtpWarehouseMapping> {

    List<HtpWarehouseMappingVO> selectHtpWarehouseMappingList(@Param(Constants.WRAPPER) LambdaQueryWrapper<HtpWarehouseMapping> eq);

    HtpWarehouseMappingVO selectOneById(Integer id);
}
