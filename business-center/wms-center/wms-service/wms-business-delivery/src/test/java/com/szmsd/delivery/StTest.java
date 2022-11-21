package com.szmsd.delivery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BusinessDeliveryApplication.class)
public class StTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void ts(){


        String no = this.createNo("NED");

        System.out.println(no);
    }


    private String createNo(String cusCode) {

        StringBuilder sb = new StringBuilder().append(cusCode);
        String keyPrefix = sb.toString();

        RedisAtomicInteger atomicInteger = new RedisAtomicInteger(keyPrefix,redisTemplate.getConnectionFactory());

        Integer no = atomicInteger.incrementAndGet();

        Long expiresTime = this.getSecondsNextEarlyMorning();
        atomicInteger.expire(expiresTime, TimeUnit.SECONDS);

        String length = String.format("%08d", no);

        return length;
    }

    /**
     * 判断当前时间距离第二天凌晨的秒数
     *
     * @return 返回值单位为[s:秒]
     */
    public Long getSecondsNextEarlyMorning() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000;
    }
}
