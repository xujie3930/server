package com.szmsd.exception.task;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.szmsd.exception.dto.ExceptionInfoQueryDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;

/**
 * todo say somthing here!
 *
 * @author jun
 */
@Slf4j
public class ExceptionEasyPoiExportTask<T> implements Runnable {
    private ExportParams exportParams;
    private Collection<T> data;
    private Class<T> clazz;
    private String filepath;
    private CountDownLatch countDownLatch;

    private Integer FileId;

    private ExceptionInfoQueryDto exceptionInfoQueryDto;

    @Override
    public void run() {
        log.info("{}-ExportTask is running", Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        try {
            ExceptionExcelUtil.export(exportParams, clazz, data,filepath,FileId,exceptionInfoQueryDto);
            log.info("{}-ExportTask is finished, cost {}ms", Thread.currentThread().getName(), System.currentTimeMillis() - start);
        }catch (OutOfMemoryError e) {
            log.error("{}-ExportTask is error,message={}", Thread.currentThread().getName(), e.getMessage());
        }finally {
            this.countDownLatch.countDown();
        }
    }

    public ExceptionEasyPoiExportTask<T> setExportParams(ExportParams exportParams) {
        this.exportParams = exportParams;
        return this;
    }

    public ExceptionEasyPoiExportTask<T> setData(Collection<T> data) {
        this.data = data;
        return this;
    }


    public ExceptionEasyPoiExportTask<T> setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }



    public ExceptionEasyPoiExportTask<T> setFilepath(String filepath) {
        this.filepath = filepath;
        return this;
    }
    public ExceptionEasyPoiExportTask<T> setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
        return this;
    }

    public ExceptionEasyPoiExportTask<T> setFileId(Integer FileId) {
        this.FileId = FileId;
        return this;
    }

    public ExceptionEasyPoiExportTask<T> setExceptionInfoQueryDto(ExceptionInfoQueryDto exceptionInfoQueryDto) {
        this.exceptionInfoQueryDto = exceptionInfoQueryDto;
        return this;
    }
}
