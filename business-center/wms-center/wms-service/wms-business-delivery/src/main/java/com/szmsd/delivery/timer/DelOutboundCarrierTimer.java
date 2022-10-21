package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class DelOutboundCarrierTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundService delOutboundService;

    @Value("${thread.trialLimit}")
    private int trialLimit;

    @Value(value = "${server.port:0}")
    private int port;


    /**
     * 定时更新7天内已完成的出库单挂号
     * <p/>
     * 每天12:00 24:00执行一次
     */
    @Async
    public void process() {
        String key = applicationName + ":DelOutboundCarrierTimer:process";
        this.doWorker(key, () -> {
            //查询范围7天内已完成的出库单
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(DelOutbound::getId, DelOutbound::getTrackingNo);
            queryWrapper.eq(DelOutbound::getState, DelOutboundStateEnum.COMPLETED.getCode());
            queryWrapper.apply("date_sub(curdate(), interval 7 day) <= create_time" +
                    " and shipment_rule in(select product_code from bas_product_service where compare_trackingno=1)");
            handle(queryWrapper);
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

    private void handle(LambdaQueryWrapper<DelOutbound> queryWrapper) {
        this.handle(queryWrapper, (DelOutbound, blank) -> async(DelOutbound));
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_CARRIER)
    public void async(DelOutbound delOutbound) {
        delOutboundService.carrierRegister(delOutbound);
    }


    private void handle(LambdaQueryWrapper<DelOutbound> queryWrapper, BiConsumer<DelOutbound, Long> consumer) {
        List<DelOutbound> delOutboundList = this.delOutboundService.list(queryWrapper);
        logger.info("定时器更新出库单挂号处理数量"+delOutboundList.size());
        if (CollectionUtils.isNotEmpty(delOutboundList)) {
            for (DelOutbound delOutbound : delOutboundList) {
                try {
                    consumer.accept(delOutbound, null);
                } catch (Exception e) {
                    logger.error("定时器更新出库单挂号{}失败{}", delOutbound.getId(), e.getMessage());
                }
            }
        }
    }
}
