package com.szmsd.pack.mapper;

import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.pack.domain.PackageAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.vo.PackageAddressVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * package - 交货管理 - 地址信息表 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
public interface PackageAddressMapper extends BaseMapper<PackageAddress> {

//    @DataScope("seller_code")
    List<PackageAddressVO> selectPackageAddressList(@Param(value = "cm") PackageMangQueryDTO packageAddress);
}
