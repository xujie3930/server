package com.szmsd.job.task;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author lc
 * @Date 2022/8/20 10:49
 * @PackageName:com.szmsd.job.task
 * @ClassName: DeliveryBringVerifyTask
 * @Description:
 * @Version 1.0
 */
@Component("deliveryBringVerifyTask")
@Slf4j
public class DeliveryBringVerifyTask {

    @Resource
    private DelOutboundFeignService feignService;

    /**
     * 通知delivery的 bringVerify任务执行
     * 因为生产的多实例，出现任务分配不均的情况
     * 尝试通过外部调用的方法，自动分发均匀执行
     */
    public void notifyBringVerify()
    {
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知提审 定时任务 开始");
        R<String> notifyBringVerify = feignService.notifyBringVerify();
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知提审 通知结果:{}", JSON.toJSONString(notifyBringVerify));
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知提审 定时任务 结束");
    }

    public void notifyAmazonLogisticsRouteId()
    {
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知获取亚马逊挂号定时任务执行任务 定时任务 开始");
        R<String> notifyBringVerify = feignService.notifyAmazonLogisticsRouteId();
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知获取亚马逊挂号定时任务执行任务 通知结果:{}", JSON.toJSONString(notifyBringVerify));
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知获取亚马逊挂号定时任务执行任务 定时任务 结束");
    }

    public void notifyWMS()
    {
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知订单执行WMS定时任务执行任务 定时任务 开始");
        R<String> notifyBringVerify = feignService.notifyWMS();
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知订单执行WMS定时任务执行任务 通知结果:{}", JSON.toJSONString(notifyBringVerify));
        log.info("[szmsd-module-job]-[DeliveryBringVerifyTask]-[notifyBringVerify] 通知订单执行WMS定时任务执行任务 定时任务 结束");
    }
}
