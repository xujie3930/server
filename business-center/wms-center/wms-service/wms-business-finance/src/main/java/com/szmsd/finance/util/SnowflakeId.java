package com.szmsd.finance.util;

import com.szmsd.common.core.utils.StringUtils;

public class SnowflakeId {
    private static final long twepoch = 1420041600000L;
    private static final long workerIdBits = 5L;
    private static final long datacenterIdBits = 5L;
    private final long maxWorkerId = 31L;
    private static final long maxDatacenterId = 31L;
    private static final long sequenceBits = 12L;
    private static final long workerIdShift = 12L;
    private static final long datacenterIdShift = 17L;
    private static final long timestampLeftShift = 22L;
    private static final long sequenceMask = 4095L;
    private static long workerId;
    private static long datacenterId;
    private static long sequence = 0L;
    private static long lastTimestamp = -1L;

    public SnowflakeId(long workerId, long datacenterId) {
        if (workerId <= 31L && workerId >= 0L) {
            if (datacenterId <= 31L && datacenterId >= 0L) {
                SnowflakeId.workerId = workerId;
                SnowflakeId.datacenterId = datacenterId;
            } else {
                throw new IllegalArgumentException(StringUtils.format("datacenter Id can't be greater than %d or less than 0", 31L));
            }
        } else {
            throw new IllegalArgumentException(StringUtils.format("worker Id can't be greater than %d or less than 0", 31L));
        }
    }

    public SnowflakeId() {
    }

    private static long _getNextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(StringUtils.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        } else {
            if (lastTimestamp == timestamp) {
                sequence = sequence + 1L & 4095L;
                if (sequence == 0L) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;
            return timestamp - 1420041600000L << 22 | datacenterId << 17 | workerId << 12 | sequence;
        }
    }

    public static String getNextId() {
        return _getNextId() + "";
    }

    public static String getNextId12() {
        String id = getNextId();
        return id.length() > 12 ? id.substring(id.length() - 12, id.length()) : id;
    }

    private static long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = timeGen(); timestamp <= lastTimestamp; timestamp = timeGen()) {
        }

        return timestamp;
    }

    private static long timeGen() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        for(int i = 0; i < 100000; ++i) {
            long id = _getNextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }

    }
}
