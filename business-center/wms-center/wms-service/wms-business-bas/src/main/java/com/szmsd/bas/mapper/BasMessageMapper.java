package com.szmsd.bas.mapper;

import com.szmsd.bas.domain.BasMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.dto.BasMessageDto;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-04-25
 */
public interface BasMessageMapper extends BaseMapper<BasMessage> {
    int insertBasMessage(BasMessageDto basMessage);

}
