package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.vo.BasSpecialOperationVo;


public interface BaseInfoMapper extends BaseMapper<BasSpecialOperation> {

    BasSpecialOperationVo selectDetailsById(int id);


}
