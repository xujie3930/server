package com.szmsd.pack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.domain.PackageAddress;
import com.szmsd.pack.dto.PackageAddressAddDTO;
import com.szmsd.pack.dto.PackageMangQueryDTO;
import com.szmsd.pack.vo.PackageAddressVO;
import com.szmsd.pack.vo.PackageMangVO;

import java.util.List;

/**
 * <p>
 * package - 交货管理 - 地址信息表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
public interface IPackageMangClientService extends IService<PackageAddress> {

    /**
     * 查询package - 交货管理 - 地址信息表模块
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return package - 交货管理 - 地址信息表模块
     */
    PackageAddressVO selectPackageAddressById(String id);

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return package - 交货管理 - 地址信息表模块集合
     */
    List<PackageAddressVO> selectPackageAddressList(PackageMangQueryDTO packageAddress);

    /**
     * 新增package - 交货管理 - 地址信息表模块
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    int insertPackageAddress(PackageAddressAddDTO packageAddress);

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageAddress package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    int updatePackageAddress(PackageAddressAddDTO packageAddress);

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    int deletePackageAddressByIds(List<String> ids);

    /**
     * 删除package - 交货管理 - 地址信息表模块信息
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    int deletePackageAddressById(String id);

    int setDefaultAddr(String id);

    List<PackageMangVO> selectPackageManagementList(PackageMangQueryDTO packageMangQueryDTO);
}

