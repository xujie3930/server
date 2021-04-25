package com.szmsd.bas.mapper;

import com.szmsd.bas.domain.BasSellerMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}
