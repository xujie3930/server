package com.szmsd.pack.service;

import com.szmsd.pack.domain.PackageManagement;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.dto.PackageMangQueryDTO;

import java.util.List;

/**
 * <p>
 * package - 交货管理 - 地址信息表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
public interface IPackageManagementService extends IService<PackageManagement> {

    /**
     * 查询package - 交货管理 - 地址信息表模块
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return package - 交货管理 - 地址信息表模块
     */
    PackageManagement selectPackageManagementById(String id);

    /**
     * 查询package - 交货管理 - 地址信息表模块列表
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return package - 交货管理 - 地址信息表模块集合
     */
    List<PackageManagement> selectPackageManagementList(PackageMangQueryDTO packageManagement);

    /**
     * 新增package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    int insertPackageManagement(PackageManagement packageManagement);

    /**
     * 修改package - 交货管理 - 地址信息表模块
     *
     * @param packageManagement package - 交货管理 - 地址信息表模块
     * @return 结果
     */
    int updatePackageManagement(PackageManagement packageManagement);

    /**
     * 批量删除package - 交货管理 - 地址信息表模块
     *
     * @param ids 需要删除的package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    int deletePackageManagementByIds(List<String> ids);

    /**
     * 删除package - 交货管理 - 地址信息表模块信息
     *
     * @param id package - 交货管理 - 地址信息表模块ID
     * @return 结果
     */
    int deletePackageManagementById(String id);

}

