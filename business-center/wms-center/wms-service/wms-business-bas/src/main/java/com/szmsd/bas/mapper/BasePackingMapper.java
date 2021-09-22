package com.szmsd.bas.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasePacking;
import com.szmsd.bas.dto.BasePackingDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author l
 * @since 2021-03-06
 */
public interface BasePackingMapper extends BaseMapper<BasePacking> {
    List<BasePackingDto> selectBasePackingGroup(@Param("packingMaterialType") String packingMaterialType);

    List<BasePackingDto> selectBasePacking(@Param("cm") BasePackingDto warehouseCode);
}
