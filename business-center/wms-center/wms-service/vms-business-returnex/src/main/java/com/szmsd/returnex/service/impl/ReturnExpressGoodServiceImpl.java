package com.szmsd.returnex.service.impl;

import com.szmsd.returnex.domain.ReturnExpressGood;
import com.szmsd.returnex.mapper.ReturnExpressGoodMapper;
import com.szmsd.returnex.service.IReturnExpressGoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * return_express - 退货单sku详情表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-03-29
 */
@Service
public class ReturnExpressGoodServiceImpl extends ServiceImpl<ReturnExpressGoodMapper, ReturnExpressGood> implements IReturnExpressGoodService {


    /**
     * 查询return_express - 退货单sku详情表模块
     *
     * @param id return_express - 退货单sku详情表模块ID
     * @return return_express - 退货单sku详情表模块
     */
    @Override
    public ReturnExpressGood selectReturnExpressGoodById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询return_express - 退货单sku详情表模块列表
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return return_express - 退货单sku详情表模块
     */
    @Override
    public List<ReturnExpressGood> selectReturnExpressGoodList(ReturnExpressGood returnExpressGood) {
        QueryWrapper<ReturnExpressGood> where = new QueryWrapper<ReturnExpressGood>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增return_express - 退货单sku详情表模块
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return 结果
     */
    @Override
    public int insertReturnExpressGood(ReturnExpressGood returnExpressGood) {
        return baseMapper.insert(returnExpressGood);
    }

    /**
     * 修改return_express - 退货单sku详情表模块
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return 结果
     */
    @Override
    public int updateReturnExpressGood(ReturnExpressGood returnExpressGood) {
        return baseMapper.updateById(returnExpressGood);
    }

    /**
     * 批量删除return_express - 退货单sku详情表模块
     *
     * @param ids 需要删除的return_express - 退货单sku详情表模块ID
     * @return 结果
     */
    @Override
    public int deleteReturnExpressGoodByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除return_express - 退货单sku详情表模块信息
     *
     * @param id return_express - 退货单sku详情表模块ID
     * @return 结果
     */
    @Override
    public int deleteReturnExpressGoodById(String id) {
        return baseMapper.deleteById(id);
    }


}

