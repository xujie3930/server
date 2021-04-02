package com.szmsd.returnex.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.returnex.domain.ReturnExpressGood;
import com.szmsd.returnex.dto.ReturnExpressGoodAddDTO;
import com.szmsd.returnex.vo.ReturnExpressGoodVO;

import javax.validation.constraints.NotNull;
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
     * 根据退件单id查询关联的商品列表
     *
     * @param id
     * @return
     */
    List<ReturnExpressGoodVO> queryGoodListByExId(@NotNull Integer id);

    /**
     * 批量 新增/修改 商品信息
     *
     * @param addOrUpdateList 商品列表
     * @param batchId         关联的退件单id
     */
    void addOrUpdateGoodInfoBatch(List<ReturnExpressGoodAddDTO> addOrUpdateList, Integer batchId);
}

