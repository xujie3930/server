package com.szmsd.inventory.mapper;

import com.szmsd.inventory.domain.PurchaseLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.vo.PurchaseLogVO;

import java.util.List;

/**
 * <p>
 * 采购单日志 Mapper 接口
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
public interface PurchaseLogMapper extends BaseMapper<PurchaseLog> {

    List<PurchaseLogVO> selectPurchaseLogList(String id);
}
