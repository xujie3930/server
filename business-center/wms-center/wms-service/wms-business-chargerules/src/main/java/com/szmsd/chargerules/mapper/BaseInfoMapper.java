package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.chargerules.domain.BasSpecialOperation;
import com.szmsd.chargerules.vo.BasSpecialOperationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BaseInfoMapper extends BaseMapper<BasSpecialOperation> {

    BasSpecialOperationVo selectDetailsById(int id);

    List<BasSpecialOperationVo> selectPageList(@Param(Constants.WRAPPER) Wrapper<BasSpecialOperation> queryWrapper);
}
