package com.szmsd.delivery;

import cn.hutool.core.lang.Snowflake;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class TestId {

    @Test
    public void id() {

        Snowflake snowflake = new Snowflake(getWorkId(), 1);

        System.out.println(snowflake.nextId());
    }

    private static Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }
}
