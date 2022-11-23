package com.szmsd.exception.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BasExcetionFileMapper extends BaseMapper<BasFile> {
    int deleteByPrimaryKey(Integer id);

    int insert(BasFile record);

    int insertSelective(BasFile record);

    BasFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasFile record);

    int updateByPrimaryKey(BasFile record);

    Integer selectDelOutboundCount(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundListQueryDto> queryWrapper);

}