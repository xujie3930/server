package com.szmsd.pack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import com.szmsd.pack.dto.PackageDeliveryConditionsDTO;

import java.util.List;

/**
 * <p>
 * 发货条件表 服务类
 * </p>
 *
 * @author admpon
 * @since 2022-03-23
 */
public interface IPackageDeliveryConditionsService extends IService<PackageDeliveryConditions> {

    /**
     * 查询发货条件表模块
     *
     * @param id 发货条件表模块ID
     * @return 发货条件表模块
     */
    PackageDeliveryConditions selectPackageDeliveryConditionsById(String id);

    PackageDeliveryConditions getInfo(PackageDeliveryConditions packageDeliveryConditions);

    /**
     * 查询发货条件表模块列表
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 发货条件表模块集合
     */
    List<PackageDeliveryConditions> selectPackageDeliveryConditionsList(PackageDeliveryConditions packageDeliveryConditions);

    /**
     * 新增发货条件表模块
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 结果
     */
    int insertPackageDeliveryConditions(PackageDeliveryConditions packageDeliveryConditions);

    /**
     * 修改发货条件表模块
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 结果
     */
    int updatePackageDeliveryConditions(PackageDeliveryConditions packageDeliveryConditions);

    /**
     * 批量删除发货条件表模块
     *
     * @param ids 需要删除的发货条件表模块ID
     * @return 结果
     */
    int deletePackageDeliveryConditionsByIds(List<String> ids);

    /**
     * 删除发货条件表模块信息
     *
     * @param id 发货条件表模块ID
     * @return 结果
     */
    int deletePackageDeliveryConditionsById(String id);

    int save(PackageDeliveryConditionsDTO dto);

}

