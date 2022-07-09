package com.szmsd.bas.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.api.domain.BasChannels;
import com.szmsd.bas.api.domain.dto.BasChannelsDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasChannelsMapper extends BaseMapper<BasChannels> {
    int deleteByPrimaryKey(Integer id);

    int insert(BasChannels record);

    int insertSelective(BasChannels record);

    BasChannels selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasChannels record);

    int updateByPrimaryKey(BasChannels record);

    List<BasChannels>  selectBasChannels(BasChannelsDTO basFbaDTO);
}