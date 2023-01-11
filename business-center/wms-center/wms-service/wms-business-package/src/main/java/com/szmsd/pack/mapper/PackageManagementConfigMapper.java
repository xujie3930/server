package com.szmsd.pack.mapper;


import com.szmsd.pack.domain.PackageManagementConfig;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.vo.PackageManagementConfigVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PackageManagementConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PackageManagementConfig record);

    int insertSelective(PackageManagementConfig record);

    List<PackageManagementConfigVo> selectByPrimaryKey(PackageMangQueryDTO packageMangQueryDTO);

    int updateByPrimaryKeySelective(PackageManagementConfig record);

    int updateByPrimaryKey(PackageManagementConfig record);

    List<PackageManagementConfig>  selectPackageManagementConfigJob();
}