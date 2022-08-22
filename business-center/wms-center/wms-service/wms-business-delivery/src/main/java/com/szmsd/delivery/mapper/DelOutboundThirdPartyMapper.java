package com.szmsd.delivery.mapper;

import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 出库单临时第三方任务表 Mapper 接口
 * </p>
 *
 * @author admin
 * @since 2022-08-22
 */
public interface DelOutboundThirdPartyMapper extends BaseMapper<DelOutboundThirdParty> {

    void updateRecord(DelOutboundThirdParty modifyDelOutboundCompleted);
}
