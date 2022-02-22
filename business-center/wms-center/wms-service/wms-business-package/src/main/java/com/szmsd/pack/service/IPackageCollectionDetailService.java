package com.szmsd.pack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.domain.PackageCollectionDetail;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * package - 交货管理 - 揽收货物 服务类
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
public interface IPackageCollectionDetailService extends IService<PackageCollectionDetail> {

    /**
     * 查询package - 交货管理 - 揽收货物模块
     *
     * @param id package - 交货管理 - 揽收货物模块ID
     * @return package - 交货管理 - 揽收货物模块
     */
    PackageCollectionDetail selectPackageCollectionDetailById(String id);

    /**
     * 查询package - 交货管理 - 揽收货物模块列表
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return package - 交货管理 - 揽收货物模块集合
     */
    List<PackageCollectionDetail> selectPackageCollectionDetailList(PackageCollectionDetail packageCollectionDetail);

    /**
     * 新增package - 交货管理 - 揽收货物模块
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return 结果
     */
    int insertPackageCollectionDetail(PackageCollectionDetail packageCollectionDetail);

    /**
     * 修改package - 交货管理 - 揽收货物模块
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return 结果
     */
    int updatePackageCollectionDetail(PackageCollectionDetail packageCollectionDetail);

    /**
     * 批量删除package - 交货管理 - 揽收货物模块
     *
     * @param ids 需要删除的package - 交货管理 - 揽收货物模块ID
     * @return 结果
     */
    int deletePackageCollectionDetailByIds(List<String> ids);

    /**
     * 删除package - 交货管理 - 揽收货物模块信息
     *
     * @param id package - 交货管理 - 揽收货物模块ID
     * @return 结果
     */
    int deletePackageCollectionDetailById(String id);

    /**
     * 揽收货物名称
     *
     * @param collectionIdList collectionIdList
     * @return Map
     */
    Map<Long, String> getCollectionSkuNames(List<Long> collectionIdList);

    List<PackageCollectionDetail> listByCollectionId(Long collectionId);
}

