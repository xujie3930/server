package com.szmsd.returnex.service;

import com.szmsd.returnex.domain.ReturnExpressGood;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * return_express - 退货单sku详情表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-03-29
 */
public interface IReturnExpressGoodService extends IService<ReturnExpressGood> {

    /**
     * 查询return_express - 退货单sku详情表模块
     *
     * @param id return_express - 退货单sku详情表模块ID
     * @return return_express - 退货单sku详情表模块
     */
    ReturnExpressGood selectReturnExpressGoodById(String id);

    /**
     * 查询return_express - 退货单sku详情表模块列表
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return return_express - 退货单sku详情表模块集合
     */
    List<ReturnExpressGood> selectReturnExpressGoodList(ReturnExpressGood returnExpressGood);

    /**
     * 新增return_express - 退货单sku详情表模块
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return 结果
     */
    int insertReturnExpressGood(ReturnExpressGood returnExpressGood);

    /**
     * 修改return_express - 退货单sku详情表模块
     *
     * @param returnExpressGood return_express - 退货单sku详情表模块
     * @return 结果
     */
    int updateReturnExpressGood(ReturnExpressGood returnExpressGood);

    /**
     * 批量删除return_express - 退货单sku详情表模块
     *
     * @param ids 需要删除的return_express - 退货单sku详情表模块ID
     * @return 结果
     */
    int deleteReturnExpressGoodByIds(List<String> ids);

    /**
     * 删除return_express - 退货单sku详情表模块信息
     *
     * @param id return_express - 退货单sku详情表模块ID
     * @return 结果
     */
    int deleteReturnExpressGoodById(String id);

}

