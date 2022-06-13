package com.szmsd.returnex.timer;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.returnex.config.ConfigStatus;
import com.szmsd.returnex.config.LockerUtil;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.service.IReturnExpressService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class ReturnExpressTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedissonClient redissonClient;
    @Value("${spring.application.name}")
    private String applicationName;
    @Resource
    private IReturnExpressService returnExpressService;

    @Resource
    private ConfigStatus configStatus;
    /**
     * 每日凌晨过期时间处理
     * <p/>
     * 每分钟执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0 0 2 * * ?")
    public void processing() {
        String key = applicationName + ":ReturnExpressTimer:processing";
        this.doWorker(key, () -> {
            process();
        });
    }
    private void process(){

        LambdaQueryWrapper<ReturnExpressDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.ne(ReturnExpressDetail::getDealStatus, configStatus.getDealStatus().getWmsFinish());
        queryWrapper.last("limit 500");
        List<ReturnExpressDetail> list = returnExpressService.list(queryWrapper);
        if(list.size() == 0){
            return;
        }
        Date nowDate = clearSFM(new Date());
        for (ReturnExpressDetail detail: list){
            if(detail.getExpireTime() == null){
                detail.setExpirationDuration(0);
            }else{

                detail.setExpirationDuration(dateCalculation(nowDate, clearSFM(detail.getExpireTime())));
            }
        }
        returnExpressService.updateBatchById(list);
        //处理到无数据
        this.process();
    }
    private Date clearSFM(Date now){
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(now);
        cal1.set(Calendar.HOUR_OF_DAY, 0);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return cal1.getTime();
    }

    /*public static void main(String[] args) {

        System.out.println((Long)3024000000L);
    }*/
    private static int dateCalculation(Date startDate, Date endDate){
        //System.out.println(DateUtil.format(startDate, "yyyy-MM-dd")+","+startDate.getTime());
        //System.out.println(DateUtil.format(endDate, "yyyy-MM-dd")+","+endDate.getTime());
        Long val = (Long)(endDate.getTime() - startDate.getTime());
        Long day = val / (1000 * 3600 * 24);
        return day.intValue();

    }


    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

}
