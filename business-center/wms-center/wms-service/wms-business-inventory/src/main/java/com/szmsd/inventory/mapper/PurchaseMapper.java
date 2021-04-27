package com.szmsd.inventory.mapper;

import com.szmsd.inventory.domain.Purchase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.dto.PurchaseQueryDTO;
import com.szmsd.inventory.domain.vo.PurchaseInfoListVO;
import com.szmsd.inventory.domain.vo.PurchaseInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 采购单 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
public interface PurchaseMapper extends BaseMapper<Purchase> {

    List<PurchaseInfoListVO> selectPurchaseList(@Param("cm") PurchaseQueryDTO purchaseQueryDTO);

    PurchaseInfoVO selectPurchaseByPurchaseNo(String purchaseNo);
}
