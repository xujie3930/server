package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.DelOutboundCharge;

import java.util.List;

/**
 * <p>
 * 出库单费用明细 Mapper 接口
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */
public interface DelOutboundChargeMapper extends BaseMapper<DelOutboundCharge> {

    List<DelOutboundCharge>  selectDelOutboundChargeList(DelOutboundCharge delOutboundCharge);
}
