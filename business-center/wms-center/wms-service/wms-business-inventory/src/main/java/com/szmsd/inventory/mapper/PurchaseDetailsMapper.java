package com.szmsd.inventory.mapper;

import com.szmsd.inventory.domain.PurchaseDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.vo.PurchaseInfoDetailVO;

import java.util.List;

/**
 * <p>
 * 采购单 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-04-26
 */
public interface PurchaseDetailsMapper extends BaseMapper<PurchaseDetails> {

    List<PurchaseInfoDetailVO> selectPurchaseInfoDetailListByAssId(Integer assId);
}
