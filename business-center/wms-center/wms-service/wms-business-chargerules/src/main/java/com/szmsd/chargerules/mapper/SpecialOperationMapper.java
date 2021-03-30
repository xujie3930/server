package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.SpecialOperation;
import org.apache.ibatis.annotations.Select;

public interface SpecialOperationMapper extends BaseMapper<SpecialOperation> {

    @Select("SELECT first_price,next_price,unit FROM cha_special_operation WHERE operation_type = #{operationType}")
    SpecialOperation detailsByOperationType(String operationType);
}
