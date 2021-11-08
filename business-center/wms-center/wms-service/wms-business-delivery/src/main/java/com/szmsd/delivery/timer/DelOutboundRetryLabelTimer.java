package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.event.DelOutboundRetryLabelEvent;
import com.szmsd.delivery.event.EventUtil;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DelOutboundRetryLabelTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundRetryLabelService delOutboundRetryLabelService;

    /**
     * 获取标签文件
     * <p/>
     * 每10s执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0/10 * * * * ?")
    public void retryLabel() {
        logger.debug("开始执行任务 - 处理完成的单据");
        // 外层锁，保证定时任务只有一个服务调用
        String key = applicationName + ":DelOutboundRetryLabelTimer:retryLabel";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelOutboundRetryLabel> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(DelOutboundRetryLabel::getId);
            queryWrapper.eq(DelOutboundRetryLabel::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            // 小于6次
            queryWrapper.lt(DelOutboundRetryLabel::getFailCount, 6);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelOutboundRetryLabel::getNextRetryTime, new Date());
            List<DelOutboundRetryLabel> list = this.delOutboundRetryLabelService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                for (DelOutboundRetryLabel retryLabel : list) {
                    EventUtil.publishEvent(new DelOutboundRetryLabelEvent(retryLabel.getId()));
                }
            }
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
