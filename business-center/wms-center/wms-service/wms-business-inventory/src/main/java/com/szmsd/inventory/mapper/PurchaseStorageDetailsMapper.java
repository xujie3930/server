package com.szmsd.inventory.mapper;

import com.szmsd.inventory.domain.PurchaseStorageDetails;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.excel.PurchaseStorageDetailsExcle;
import com.szmsd.inventory.domain.excel.PurchaseStorageDetailsExclesp;
import com.szmsd.inventory.domain.vo.PurchaseStorageDetailsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

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

    List<PurchaseStorageDetailsExcle> selectPurchaseStorageDetailsExcleListByAssId(@Param("productName") String productName,@Param("assId") Integer assId);

    List<PurchaseStorageDetailsExcle> selectPurchaseStorageDetailsExcleListByAssIds(@Param("productName") String productName,@Param("assId") Integer assId);

    //异常返回实体
    List<PurchaseStorageDetailsExclesp> selectPurchaseStorageDetailsExcleListByAssIdsp(@Param("productName") String productName, @Param("assId") Integer assId);



    List<PurchaseStorageDetailsVO>  selectPurchaseStorageDetailsVO(PurchaseStorageDetailsExcle purchaseStorageDetailsExcle);

    int insertSelectiveus(PurchaseStorageDetails purchaseStorageDetails);

    void  deletePurchaseStorageDetails(Integer id);
}
