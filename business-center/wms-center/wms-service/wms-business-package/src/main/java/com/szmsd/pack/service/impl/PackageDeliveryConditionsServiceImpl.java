package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import com.szmsd.pack.dto.PackageDeliveryConditionsDTO;
import com.szmsd.pack.mapper.PackageDeliveryConditionsMapper;
import com.szmsd.pack.service.IPackageDeliveryConditionsService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 发货条件表 服务实现类
 * </p>
 *
 * @author admpon
 * @since 2022-03-23
 */
@Service
public class PackageDeliveryConditionsServiceImpl extends ServiceImpl<PackageDeliveryConditionsMapper, PackageDeliveryConditions> implements IPackageDeliveryConditionsService {


    /**
     * 查询发货条件表模块
     *
     * @param id 发货条件表模块ID
     * @return 发货条件表模块
     */
    @Override
    public PackageDeliveryConditions selectPackageDeliveryConditionsById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public PackageDeliveryConditions getInfo(PackageDeliveryConditions packageDeliveryConditions) {
        LambdaQueryWrapper<PackageDeliveryConditions> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(PackageDeliveryConditions::getWarehouseCode, packageDeliveryConditions.getWarehouseCode());
        queryWrapper.eq(PackageDeliveryConditions::getProductCode, packageDeliveryConditions.getProductCode());
        queryWrapper.last("LIMIT 1");
        return super.getOne(queryWrapper);
    }

    /**
     * 查询发货条件表模块列表
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 发货条件表模块
     */
    @Override
    public List<PackageDeliveryConditions> selectPackageDeliveryConditionsList(PackageDeliveryConditions packageDeliveryConditions) {
        QueryWrapper<PackageDeliveryConditions> where = new QueryWrapper<PackageDeliveryConditions>();
        where.like(StringUtils.isNotEmpty(packageDeliveryConditions.getProductCode()),"product_code", packageDeliveryConditions.getProductCode())
                .like(StringUtils.isNotEmpty(packageDeliveryConditions.getWarehouseCode()),"warehouse_code", packageDeliveryConditions.getWarehouseCode())
                .eq(StringUtils.isNotEmpty(packageDeliveryConditions.getCommandNodeCode()),"command_node_code", packageDeliveryConditions.getCommandNodeCode())
                .eq(StringUtils.isNotEmpty(packageDeliveryConditions.getStatus()),"status", packageDeliveryConditions.getStatus());
        return baseMapper.selectList(where);
    }

    /**
     * 新增发货条件表模块
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 结果
     */
    @Override
    public int insertPackageDeliveryConditions(PackageDeliveryConditions packageDeliveryConditions) {
        return baseMapper.insert(packageDeliveryConditions);
    }

    /**
     * 修改发货条件表模块
     *
     * @param packageDeliveryConditions 发货条件表模块
     * @return 结果
     */
    @Override
    public int updatePackageDeliveryConditions(PackageDeliveryConditions packageDeliveryConditions) {
        return baseMapper.updateById(packageDeliveryConditions);
    }

    /**
     * 批量删除发货条件表模块
     *
     * @param ids 需要删除的发货条件表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageDeliveryConditionsByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除发货条件表模块信息
     *
     * @param id 发货条件表模块ID
     * @return 结果
     */
    @Override
    public int deletePackageDeliveryConditionsById(String id) {
        return baseMapper.deleteById(id);
    }


    @Transactional
    @Override
    public int save(PackageDeliveryConditionsDTO dto) {
        PackageDeliveryConditions domain = new PackageDeliveryConditions();
        BeanUtils.copyProperties(dto, domain);
        baseMapper.insert(domain);
        return 1;
    }

}

