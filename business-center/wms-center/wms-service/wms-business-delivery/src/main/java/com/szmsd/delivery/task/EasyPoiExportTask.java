package com.szmsd.delivery.task;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.szmsd.delivery.util.ExParams;
import com.szmsd.delivery.util.ExcelUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * todo say somthing here!
 *
 * @author jun
 */
@Slf4j
public class EasyPoiExportTask<T,V> implements Runnable {
    private ExportParams exportParams;
    private Collection<T> data;
    private Collection<V> data2;
    private Class<T> clazz;
    private Class<V> clazz2;
    private String filepath;
    private CountDownLatch countDownLatch;

    @Override
    public void run() {
        log.info("{}-ExportTask is running", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        try {
            ExcelUtil.export(exportParams, clazz, data,data2,clazz2,filepath);
            log.info("{}-ExportTask is finished, cost {}ms", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        }catch (OutOfMemoryError e) {
            log.error("{}-ExportTask is error,message={}", Thread.currentThread().getName(), e.getMessage());
        }finally {
            this.countDownLatch.countDown();
        }
    }

    public EasyPoiExportTask<T,V> setExportParams(ExportParams exportParams) {
        this.exportParams = exportParams;
        return this;
    }

    public EasyPoiExportTask<T,V> setData(Collection<T> data) {
        this.data = data;
        return this;
    }


    public EasyPoiExportTask<T,V> setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }
    public EasyPoiExportTask<T,V> setData2(Collection<V> data2) {
        this.data2 = data2;
        return this;
    }

    public EasyPoiExportTask<T,V> setClazz2(Class<V> clazz2) {
        this.clazz2 = clazz2;
        return this;
    }
    public EasyPoiExportTask<T,V> setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }
    public EasyPoiExportTask<T,V> setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        return this;
    }
}
