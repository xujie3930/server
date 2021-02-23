package com.szmsd.common.core.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 添加记录编号
 */
public class RecordNoUtil {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final AtomicInteger atomicInteger = new AtomicInteger(1000000);


    /**
     * 获取扫描单号
     * @param scanType
     * @return
     */
    public static synchronized String getRecordNo(String scanType) {


        atomicInteger.getAndIncrement();
        int i = atomicInteger.get();
        String date = simpleDateFormat.format(new Date());
        if(StringUtils.isEmpty(scanType))
        {
            return date  + i;

        }
        return date + scanType + i;
    }

    /**
     * 不哦去问题件编号
     * @param wayBillNo
     * @return
     */
    public static String getProblemNo(String wayBillNo) {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return id + "问题件" + wayBillNo;

    }

    public static void main(String[] args) {
        String recordNo = getRecordNo(null);
        System.out.println(recordNo);

    }


}
