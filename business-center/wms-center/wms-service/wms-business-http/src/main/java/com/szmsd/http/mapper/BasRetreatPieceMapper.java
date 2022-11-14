package com.szmsd.http.mapper;


import com.szmsd.http.domain.BasRetreatPiece;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface BasRetreatPieceMapper {
    int deleteByPrimaryKey(@Param("codeKey") String codeKey);

    int insert(BasRetreatPiece record);

    int insertSelective(@Param("map") Map map);

    BasRetreatPiece selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasRetreatPiece record);

    int updateByPrimaryKey(BasRetreatPiece record);
}