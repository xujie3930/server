package com.szmsd.ec.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.dto.LabelCountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 电商平台公共订单表 Mapper 接口
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
public interface CommonOrderMapper extends BaseMapper<CommonOrder> {

    List<LabelCountDTO> selectCountByStatus(@Param(Constants.WRAPPER) Wrapper<CommonOrder> queryWrapper);
}
