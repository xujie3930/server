package com.szmsd.pack.mapper;


import com.szmsd.pack.domain.PackageManagementConfigWeek;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PackageManagementConfigWeekMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PackageManagementConfigWeek record);

    int insertSelective(PackageManagementConfigWeek record);

    PackageManagementConfigWeek selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PackageManagementConfigWeek record);

    int updateByPrimaryKey(PackageManagementConfigWeek record);
}