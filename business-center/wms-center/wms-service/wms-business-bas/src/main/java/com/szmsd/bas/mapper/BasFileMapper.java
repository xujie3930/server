package com.szmsd.bas.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasFileMapper extends BaseMapper<BasFile> {

    int deleteByPrimaryKey(Integer id);

    int insert(BasFile record);

    int insertSelective(BasFile record);

    BasFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasFile record);

    int updateByPrimaryKey(BasFile record);

    List<String> selectModularName();
}