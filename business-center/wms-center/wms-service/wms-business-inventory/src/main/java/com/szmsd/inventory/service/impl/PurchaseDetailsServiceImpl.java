package com.szmsd.inventory.service.impl;

import com.szmsd.inventory.domain.PurchaseDetails;
import com.szmsd.inventory.mapper.PurchaseDetailsMapper;
import com.szmsd.inventory.service.IPurchaseDetailsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * 采购单 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
@Service
public class PurchaseDetailsServiceImpl extends ServiceImpl<PurchaseDetailsMapper, PurchaseDetails> implements IPurchaseDetailsService {


    /**
     * 查询采购单模块
     *
     * @param id 采购单模块ID
     * @return 采购单模块
     */
    @Override
    public PurchaseDetails selectPurchaseDetailsById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询采购单模块列表
     *
     * @param purchaseDetails 采购单模块
     * @return 采购单模块
     */
    @Override
    public List<PurchaseDetails> selectPurchaseDetailsList(PurchaseDetails purchaseDetails) {
        QueryWrapper<PurchaseDetails> where = new QueryWrapper<PurchaseDetails>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增采购单模块
     *
     * @param purchaseDetails 采购单模块
     * @return 结果
     */
    @Override
    public int insertPurchaseDetails(PurchaseDetails purchaseDetails) {
        return baseMapper.insert(purchaseDetails);
    }

    /**
     * 修改采购单模块
     *
     * @param purchaseDetails 采购单模块
     * @return 结果
     */
    @Override
    public int updatePurchaseDetails(PurchaseDetails purchaseDetails) {
        return baseMapper.updateById(purchaseDetails);
    }

    /**
     * 批量删除采购单模块
     *
     * @param ids 需要删除的采购单模块ID
     * @return 结果
     */
    @Override
    public int deletePurchaseDetailsByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除采购单模块信息
     *
     * @param id 采购单模块ID
     * @return 结果
     */
    @Override
    public int deletePurchaseDetailsById(String id) {
        return baseMapper.deleteById(id);
    }


}

