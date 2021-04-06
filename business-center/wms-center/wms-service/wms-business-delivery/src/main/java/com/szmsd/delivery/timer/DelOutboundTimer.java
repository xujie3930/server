package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class DelOutboundTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IDelOutboundService delOutboundService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;

    /**
     * 处理已完成的出库单
     * <p/>
     * 每分钟执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0 * * * * ?")
    public void completed() {
        logger.info("开始执行任务");
        // 查询初始化和失败的任务执行
        LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
        List<DelOutboundCompleted> delOutboundCompletedList = this.delOutboundCompletedService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(delOutboundCompletedList)) {
            for (DelOutboundCompleted delOutboundCompleted : delOutboundCompletedList) {
                try {
                    this.delOutboundService.completed(delOutboundCompleted.getOrderNo());
                    // 处理成功
                    this.delOutboundCompletedService.success(delOutboundCompleted.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    // 处理失败
                    this.delOutboundCompletedService.fail(delOutboundCompleted.getId());
                }
            }
        }
    }
}
