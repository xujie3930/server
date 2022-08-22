package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.bas.api.factory.BasFeignServiceFallbackFactory;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
import com.szmsd.delivery.event.DelOutboundOperationLogEnum;
import com.szmsd.delivery.mapper.DelOutboundThirdPartyMapper;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.service.wrapper.DelOutboundWrapperContext;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import com.szmsd.http.dto.ShipmentOrderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.List;

/**
* <p>
    * 出库单临时第三方任务表 服务实现类
    * </p>
*
* @author admin
* @since 2022-08-22
*/
@Service
public class DelOutboundThirdPartyServiceImpl extends ServiceImpl<DelOutboundThirdPartyMapper, DelOutboundThirdParty> implements IDelOutboundThirdPartyService {
    private static final Logger logger = LoggerFactory.getLogger(DelOutboundThirdPartyServiceImpl.class);

    @Autowired
    @Lazy
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;
    @Override
    public void thirdParty(String orderNo, String amazonLogisticsRouteId) {



        StopWatch stopWatch = new StopWatch();

        // 创建亚马逊承运商物流订单
        IDelOutboundBringVerifyService delOutboundBringVerifyService = SpringUtils.getBean(IDelOutboundBringVerifyService.class);
        stopWatch.start();
        DelOutbound dataDelOutbound = delOutboundService.getByOrderNo(orderNo);
        dataDelOutbound.setAmazonLogisticsRouteId(amazonLogisticsRouteId);
        DelOutboundWrapperContext delOutboundWrapperContext = this.delOutboundBringVerifyService.initContext(delOutboundService.getByOrderNo(orderNo));
        try {
            delOutboundBringVerifyService.shipmentAmazonOrder(delOutboundWrapperContext);
            stopWatch.stop();
            logger.info("创建亚马逊承运商物流订单成功{},耗时{}", dataDelOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
        }catch (Exception e){
            stopWatch.stop();
            logger.info("创建亚马逊承运商物流订单失败{},耗时{}", dataDelOutbound.getOrderNo(), stopWatch.getLastTaskInfo().getTimeMillis());
            throw new RuntimeException(e);
        }
        IDelOutboundService delOutboundService = SpringUtils.getBean(IDelOutboundService.class);
        DelOutbound updateDelOutbound = new DelOutbound();
        updateDelOutbound.setId(dataDelOutbound.getId());
        updateDelOutbound.setAmazonLogisticsRouteId(amazonLogisticsRouteId);
        delOutboundService.updateById(updateDelOutbound);
        DelOutboundOperationLogEnum.BRV_SHIPMENT_AMAZON_ORDER.listener(dataDelOutbound);



    }
    @Transactional
    @Override
    public void fail(Long id, String remark) {
        DelOutboundThirdParty  modifyDelOutboundCompleted = new DelOutboundThirdParty();
        modifyDelOutboundCompleted.setId(id);
        // 修改状态为失败
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.FAIL.getCode());
        if (remark.length() > 500) {
            remark = remark.substring(0, 200);
        }
        modifyDelOutboundCompleted.setRemark(remark);
        // 处理次数累加，下一次处理时间 = 系统当前时间 + 5
        this.baseMapper.updateRecord(modifyDelOutboundCompleted);
    }

    @Transactional
    @Override
    public void success(Long id) {
        DelOutboundThirdParty modifyDelOutboundCompleted = new DelOutboundThirdParty();
        modifyDelOutboundCompleted.setId(id);
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.SUCCESS.getCode());
        this.updateById(modifyDelOutboundCompleted);
    }



}

