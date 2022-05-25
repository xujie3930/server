package com.szmsd.delivery;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestTimeInterval {

    @Test
    public void testTimeInterval() throws InterruptedException {
        TimeInterval timer = DateUtil.timer();

        TimeUnit.SECONDS.sleep(1);

        System.out.println(timer.intervalRestart());

        TimeUnit.SECONDS.sleep(1);

        System.out.println(timer.intervalRestart());

        TimeUnit.SECONDS.sleep(1);

        System.out.println(timer.intervalRestart());

        TimeUnit.SECONDS.sleep(1);

        System.out.println(timer.interval());
    }
}
