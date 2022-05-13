package com.szmsd.ec.common.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szmsd.ec.common.event.ShopifyFulfillmentEvent;
import com.szmsd.ec.common.service.ICommonOrderService;
import com.szmsd.ec.domain.CommonOrder;
import com.szmsd.ec.enums.OrderSourceEnum;
import com.szmsd.ec.enums.OrderStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 定时任务创建未完成履约单的shopify订单
 */
@Slf4j
@Component
public class CreateFulfillmentTask {

    @Autowired
    private ICommonOrderService commonOrderService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(fixedDelay = 1000*60)
    public void runFailOrder(){
        log.info("进入创建履约单定时任务========开始查询未完成履约单的订单");
        // 查询出shopify 未生成履行单成功的
        List<CommonOrder> commonOrders = commonOrderService.list(new LambdaQueryWrapper<CommonOrder>().eq(CommonOrder::getOrderSource, OrderSourceEnum.Shopify.toString())
                .eq(CommonOrder::getStatus, OrderStatusEnum.Shipped.toString())
                .and(wrapper -> wrapper.eq(CommonOrder::getFulfillmentStatus, "0").or().isNull(CommonOrder::getFulfillmentStatus)));
        if (CollectionUtils.isEmpty(commonOrders)) {
            log.info("未查询出需要执行创建履约单的订单");
            return;
        }
        log.info("需要执行创建履约单订单数量：{}", commonOrders.size());
        commonOrders.forEach(order -> applicationContext.publishEvent(new ShopifyFulfillmentEvent(order)));
        log.info("完成创建履约单");
    }
}
