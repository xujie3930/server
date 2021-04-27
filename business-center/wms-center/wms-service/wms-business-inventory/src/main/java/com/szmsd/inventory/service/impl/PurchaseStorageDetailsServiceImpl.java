package com.szmsd.inventory.service.impl;

import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.szmsd.inventory.mapper.PurchaseStorageDetailsMapper;
import com.szmsd.inventory.service.IPurchaseStorageDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * 采购单 入库详情 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
@Service
public class PurchaseStorageDetailsServiceImpl extends ServiceImpl<PurchaseStorageDetailsMapper, PurchaseStorageDetails> implements IPurchaseStorageDetailsService {


    /**
     * 查询采购单 入库详情模块
     *
     * @param id 采购单 入库详情模块ID
     * @return 采购单 入库详情模块
     */
    @Override
    public PurchaseStorageDetails selectPurchaseStorageDetailsById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询采购单 入库详情模块列表
     *
     * @param purchaseStorageDetails 采购单 入库详情模块
     * @return 采购单 入库详情模块
     */
    @Override
    public List<PurchaseStorageDetails> selectPurchaseStorageDetailsList(PurchaseStorageDetails purchaseStorageDetails) {
        QueryWrapper<PurchaseStorageDetails> where = new QueryWrapper<PurchaseStorageDetails>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增采购单 入库详情模块
     *
     * @param purchaseStorageDetails 采购单 入库详情模块
     * @return 结果
     */
    @Override
    public int insertPurchaseStorageDetails(PurchaseStorageDetails purchaseStorageDetails) {
        return baseMapper.insert(purchaseStorageDetails);
    }

    /**
     * 修改采购单 入库详情模块
     *
     * @param purchaseStorageDetails 采购单 入库详情模块
     * @return 结果
     */
    @Override
    public int updatePurchaseStorageDetails(PurchaseStorageDetails purchaseStorageDetails) {
        return baseMapper.updateById(purchaseStorageDetails);
    }

    /**
     * 批量删除采购单 入库详情模块
     *
     * @param ids 需要删除的采购单 入库详情模块ID
     * @return 结果
     */
    @Override
    public int deletePurchaseStorageDetailsByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除采购单 入库详情模块信息
     *
     * @param id 采购单 入库详情模块ID
     * @return 结果
     */
    @Override
    public int deletePurchaseStorageDetailsById(String id) {
        return baseMapper.deleteById(id);
    }


}

