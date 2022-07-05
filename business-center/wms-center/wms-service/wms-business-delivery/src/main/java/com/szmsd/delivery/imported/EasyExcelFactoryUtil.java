package com.szmsd.delivery.imported;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;

import java.io.InputStream;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 19:31
 */
public class EasyExcelFactoryUtil {

    /**
     * 读取数据
     *
     * @param inputStream   inputStream
     * @param clazz         clazz
     * @param sheetNo       sheetNo
     * @param headRowNumber headRowNumber
     * @param <T>           <T>
     * @return DefaultAnalysisEventListener<T>
     */
    public static <T> DefaultAnalysisEventListener<T> read(InputStream inputStream, Class<T> clazz, int sheetNo, int headRowNumber) {
        return read(inputStream, clazz, sheetNo, headRowNumber, null);
    }

    public static <T> DefaultAnalysisEventListener<T> read(InputStream inputStream, Class<T> clazz, int sheetNo, int headRowNumber, DefaultAnalysisFormat<T> defaultAnalysisFormat) {
        DefaultAnalysisEventListener<T> defaultAnalysisEventListener = new DefaultAnalysisEventListener<>(defaultAnalysisFormat);
        ExcelReaderSheetBuilder excelReaderSheetBuilder = EasyExcelFactory.read(inputStream, clazz, defaultAnalysisEventListener).sheet(sheetNo);
        excelReaderSheetBuilder.build().setHeadRowNumber(headRowNumber);
        excelReaderSheetBuilder.doRead();
        return defaultAnalysisEventListener;
    }
}
