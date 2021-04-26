package com.szmsd.bas.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.bas.domain.BasSellerMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.dto.BasMessageDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-04-25
 */
public interface BasSellerMessageMapper extends BaseMapper<BasSellerMessage> {
    int deleteBasSellerMessage(@Param("ids") List<Long> ids);
    List<BasMessageDto> selectBasSellerMessage(@Param(Constants.WRAPPER) QueryWrapper<BasSellerMessage> queryWrapper);

}
