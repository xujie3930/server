package com.szmsd.delivery.task;

import com.szmsd.delivery.util.ExParams;
import com.szmsd.delivery.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;


import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * todo say somthing here!
 *
 * @author
 */
@Slf4j
public class PoiExportTask<T,V> implements Runnable {
    private ExParams exParams;
    private Collection<T> data;

    private Collection<V> data2;
    private Class<T> clazz;

    private Class<V> clazz2;

    private String filepath;
    private CountDownLatch countDownLatch;

    private Integer FileId;

    @Override
    public void run() {
        log.info("{}-PoiExportTask is running", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        try {
            ExcelUtil.exportBySxssf(exParams, clazz, data,data2,clazz2,filepath,FileId);
            log.info("{}-PoiExportTask is finished, cost {}ms", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        }catch (OutOfMemoryError e) {
            log.error("{}-PoiExportTask is error,message={}", Thread.currentThread().getName(), e.getMessage());
        }finally {
            this.countDownLatch.countDown();
        }
    }

    public PoiExportTask<T,V> setExParams(ExParams exParams) {
        this.exParams = exParams;
        return this;
    }

    public PoiExportTask<T,V> setData(Collection<T> data) {
        this.data = data;
        return this;
    }


    public PoiExportTask<T,V> setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }
    public PoiExportTask<T,V> setData2(Collection<V> data2) {
        this.data2 = data2;
        return this;
    }

    public PoiExportTask<T,V> setClazz2(Class<V> clazz2) {
        this.clazz2 = clazz2;
        return this;
    }
    public PoiExportTask<T,V> setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }
    public PoiExportTask<T,V> setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        return this;
    }

    public PoiExportTask<T,V> setFileId(Integer FileId) {
        this.FileId = FileId;
        return this;
    }
}
