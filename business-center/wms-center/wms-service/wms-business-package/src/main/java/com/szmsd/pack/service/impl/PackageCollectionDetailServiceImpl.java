package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.pack.domain.PackageCollectionDetail;
import com.szmsd.pack.mapper.PackageCollectionDetailMapper;
import com.szmsd.pack.service.IPackageCollectionDetailService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * package - 交货管理 - 揽收货物 服务实现类
 * </p>
 *
 * @author asd
 * @since 2022-02-17
 */
@Service
public class PackageCollectionDetailServiceImpl extends ServiceImpl<PackageCollectionDetailMapper, PackageCollectionDetail> implements IPackageCollectionDetailService {


    /**
     * 查询package - 交货管理 - 揽收货物模块
     *
     * @param id package - 交货管理 - 揽收货物模块ID
     * @return package - 交货管理 - 揽收货物模块
     */
    @Override
    public PackageCollectionDetail selectPackageCollectionDetailById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询package - 交货管理 - 揽收货物模块列表
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return package - 交货管理 - 揽收货物模块
     */
    @Override
    public List<PackageCollectionDetail> selectPackageCollectionDetailList(PackageCollectionDetail packageCollectionDetail) {
        QueryWrapper<PackageCollectionDetail> where = new QueryWrapper<PackageCollectionDetail>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增package - 交货管理 - 揽收货物模块
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return 结果
     */
    @Override
    public int insertPackageCollectionDetail(PackageCollectionDetail packageCollectionDetail) {
        return baseMapper.insert(packageCollectionDetail);
    }

    /**
     * 修改package - 交货管理 - 揽收货物模块
     *
     * @param packageCollectionDetail package - 交货管理 - 揽收货物模块
     * @return 结果
     */
    @Override
    public int updatePackageCollectionDetail(PackageCollectionDetail packageCollectionDetail) {
        return baseMapper.updateById(packageCollectionDetail);
    }

    /**
     * 批量删除package - 交货管理 - 揽收货物模块
     *
     * @param ids 需要删除的package - 交货管理 - 揽收货物模块ID
     * @return 结果
     */
    @Override
    public int deletePackageCollectionDetailByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除package - 交货管理 - 揽收货物模块信息
     *
     * @param id package - 交货管理 - 揽收货物模块ID
     * @return 结果
     */
    @Override
    public int deletePackageCollectionDetailById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public Map<Long, String> getCollectionSkuNames(List<Long> collectionIdList) {
        if (CollectionUtils.isEmpty(collectionIdList)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<PackageCollectionDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.select(PackageCollectionDetail::getCollectionId, PackageCollectionDetail::getSkuName);
        queryWrapper.in(PackageCollectionDetail::getCollectionId, collectionIdList);
        List<PackageCollectionDetail> settingItemCustomerList = super.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(settingItemCustomerList)) {
            return settingItemCustomerList.stream().collect(Collectors.groupingBy(PackageCollectionDetail::getCollectionId, Collectors.mapping(PackageCollectionDetail::getSkuName, Collectors.joining(","))));
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public List<PackageCollectionDetail> listByCollectionId(Long collectionId) {
        LambdaQueryWrapper<PackageCollectionDetail> packageCollectionDetailLambdaQueryWrapper = Wrappers.lambdaQuery();
        packageCollectionDetailLambdaQueryWrapper.eq(PackageCollectionDetail::getCollectionId, collectionId);
        packageCollectionDetailLambdaQueryWrapper.orderByAsc(PackageCollectionDetail::getSort);
        return super.list(packageCollectionDetailLambdaQueryWrapper);
    }
}

