package com.szmsd.bas.mapper;

import com.szmsd.bas.domain.BaseProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-03-04
 */
public interface BaseProductMapper extends BaseMapper<BaseProduct> {

    int delBaseProductByPhysics(@Param("ids") List<Long> id);

}
