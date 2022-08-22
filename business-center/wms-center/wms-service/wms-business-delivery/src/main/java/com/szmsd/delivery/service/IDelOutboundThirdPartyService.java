package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* <p>
    * 出库单临时第三方任务表 服务类
    * </p>
*
* @author admin
* @since 2022-08-22
*/
public interface IDelOutboundThirdPartyService extends IService<DelOutboundThirdParty> {

        /**
        * 删除出库单临时第三方任务表模块信息
        *
        * @param id 出库单临时第三方任务表模块ID
        * @return 结果
        */
        void thirdParty(String orderNo, String amazonLogisticsRouteId);

    void success(Long id);

        void fail(Long id, String message);
}

