package com.szmsd.bas.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.szmsd.bas.domain.BasFba;
import com.szmsd.bas.dto.BasFbaDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BasFbaMapper extends BaseMapper<BasFba> {
    int deleteByPrimaryKey(@Param("id") Integer id);

    int insert(BasFba record);

    int insertSelective(BasFba record);

    BasFba selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasFba record);

    int updateByPrimaryKey(BasFba record);

    List<BasFba>  selectBasFbaList(BasFbaDTO basFbaDTO);
}