package com.szmsd.bas.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasChannelDate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BasChannelDateMapper extends BaseMapper<BasChannelDate> {
    int deleteByPrimaryKey(Integer id);

    int insert(BasChannelDate record);

    int insertSelective(BasChannelDate record);

    BasChannelDate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasChannelDate record);

    int updateByPrimaryKeyWithBLOBs(BasChannelDate record);

    int updateByPrimaryKey(BasChannelDate record);

    List<BasChannelDate>  selectListChannelDate(@Param("id") Integer id);
}