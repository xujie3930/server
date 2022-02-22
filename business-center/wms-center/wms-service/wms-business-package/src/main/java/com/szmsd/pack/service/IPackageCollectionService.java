package com.szmsd.pack.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.dto.PackageCollectionQueryDto;

import java.util.List;

/**
 * <p>
 * package - 交货管理 - 揽收 服务类
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
public interface IPackageCollectionService extends IService<PackageCollection> {

    /**
     * 查询package - 交货管理 - 揽收模块
     *
     * @param id package - 交货管理 - 揽收模块ID
     * @return package - 交货管理 - 揽收模块
     */
    PackageCollection selectPackageCollectionById(String id);

    PackageCollection selectPackageCollectionByNo(String no);

    /**
     * 查询package - 交货管理 - 揽收模块列表
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return package - 交货管理 - 揽收模块集合
     */
    List<PackageCollection> selectPackageCollectionList(PackageCollection packageCollection);

    /**
     * 新增package - 交货管理 - 揽收模块
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return 结果
     */
    int insertPackageCollection(PackageCollection packageCollection);

    /**
     * 修改package - 交货管理 - 揽收模块
     *
     * @param packageCollection package - 交货管理 - 揽收模块
     * @return 结果
     */
    int updatePackageCollection(PackageCollection packageCollection);

    int updatePackageCollectionPlan(PackageCollection packageCollection);

    int cancel(List<Long> idList);

    void notRecordCancel(PackageCollection packageCollection);

    /**
     * 批量删除package - 交货管理 - 揽收模块
     *
     * @param ids 需要删除的package - 交货管理 - 揽收模块ID
     * @return 结果
     */
    int deletePackageCollectionByIds(List<String> ids);

    /**
     * 删除package - 交货管理 - 揽收模块信息
     *
     * @param id package - 交货管理 - 揽收模块ID
     * @return 结果
     */
    int deletePackageCollectionById(String id);

    /**
     * 分页
     *
     * @param dto dto
     * @return 结果
     */
    IPage<PackageCollection> page(PackageCollectionQueryDto dto);

    int updateCollecting(String collectionNo);
}

