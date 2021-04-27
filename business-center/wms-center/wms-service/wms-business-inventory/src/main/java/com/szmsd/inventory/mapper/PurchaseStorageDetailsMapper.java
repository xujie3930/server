package com.szmsd.inventory.mapper;

import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.vo.PurchaseStorageDetailsVO;

import java.util.List;

/**
 * <p>
 * 采购单 入库详情 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
public interface PurchaseStorageDetailsMapper extends BaseMapper<PurchaseStorageDetails> {
    List<PurchaseStorageDetailsVO> selectPurchaseStorageDetailsListByAssId(Integer assId);
}
