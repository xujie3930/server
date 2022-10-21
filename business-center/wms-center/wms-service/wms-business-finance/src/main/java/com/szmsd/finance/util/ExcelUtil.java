package com.szmsd.finance.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelUtil {

    /**
     * EasyExcel 填充报表
     * @param response
     * @param map              单数据
     * @param sheetKey         第一个表数据集合key
     * @param sheetAndDataMap  第一个表数据集合
     * @param businessKey      第二个表数据集合key
     * @param otherMapData     第二个表数据集合
     * @param filename         文件名称
     * @param inputStream
     */
    public static void fillReportWithEasyExcel(HttpServletResponse response,
                                               String title,
                                               Map<Integer,List<?>> titleDataMap,
                                               String sheetKey,
                                               Map<Integer, List<?>> sheetAndDataMap,
                                               String businessKey,
                                               Map<Integer,List<?>> otherMapData,
                                               String filename, InputStream inputStream){
        //需要合并的列
        int [] mergeCollindex = {0,1,2};
        //从第一行开始
        int mergeRowIndex = 1;

        fillReportWithEasyExcel(response,title,titleDataMap,sheetKey,sheetAndDataMap,businessKey,otherMapData,filename,inputStream,mergeRowIndex,mergeCollindex);
    }

    /**
     * EasyExcel 填充报表
     * @param response
     * @param map              单数据
     * @param sheetKey         第一个表数据集合key
     * @param sheetAndDataMap  第一个表数据集合
     * @param businessKey      第二个表数据集合key
     * @param otherMapData     第二个表数据集合
     * @param filename         文件名称
     * @param inputStream
     */
    public static void fillReportWithEasyExcel(HttpServletResponse response,
                                               String title,
                                               Map<Integer,List<?>> titleDataMap,
                                               String sheetKey,
                                               Map<Integer, List<?>> sheetAndDataMap,
                                               String businessKey,
                                               Map<Integer,List<?>> otherMapData,
                                               String filename, InputStream inputStream,
                                               int mergeRowIndex,int[] mergeCollindex){
        ExcelWriter excelWriter = null;

        //需要合并的列
        //int [] mergeCollindex = {0,1,2};

        //从第一行开始
        //int mergeRowIndex = 1;

        try {
            OutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment; filename=" + filename);
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "No-cache");//设置头
            response.setHeader("Cache-Control", "no-cache");//设置头
            response.setDateHeader("Expires", 0);//设置日期头
            excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).registerWriteHandler(new MergeStrategy(mergeCollindex,mergeRowIndex)).build();
            for(Map.Entry<Integer, List<?>> entry : sheetAndDataMap.entrySet()){
                List<?> value = entry.getValue();
                Integer key = entry.getKey();

                List<?> threeData = null;
                if(otherMapData != null){
                    threeData = otherMapData.get(key);
                }

                List<?> titleData = null;
                if(titleDataMap != null){
                    titleData = titleDataMap.get(key);
                }

                WriteSheet writeSheet = EasyExcel.writerSheet(key).build();
                FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                excelWriter.fill(new FillWrapper(sheetKey,value),fillConfig, writeSheet);

                if(threeData != null) {
                    excelWriter.fill(new FillWrapper(businessKey, threeData),fillConfig, writeSheet);
                }

                if(titleData != null) {
                    excelWriter.fill(new FillWrapper(title, titleData),fillConfig, writeSheet);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

            excelWriter.finish();

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出file
     * @param file
     * @param title
     * @param titleDataMap
     * @param sheetKey
     * @param sheetAndDataMap
     * @param businessKey
     * @param otherMapData
     * @param inputStream
     */
    public static void exportFile(File file,String title,
                                               ConcurrentHashMap<Integer,List<?>> titleDataMap,
                                               String sheetKey,
                                               ConcurrentHashMap<Integer, List<?>> sheetAndDataMap,
                                               String businessKey,
                                               ConcurrentHashMap<Integer,List<?>> otherMapData, InputStream inputStream){
        //需要合并的列
        int [] mergeCollindex = {0,1,2};
        //从第一行开始
        int mergeRowIndex = 1;

        exportFile(file,title,titleDataMap,sheetKey,sheetAndDataMap,businessKey,otherMapData,inputStream,mergeRowIndex,mergeCollindex);
    }

    /**
     * 导出file
     * @param file
     * @param title
     * @param titleDataMap
     * @param sheetKey
     * @param sheetAndDataMap
     * @param businessKey
     * @param otherMapData
     * @param inputStream
     */
    public static void exportFile(File file,String title,
                                  ConcurrentHashMap<Integer,List<?>> titleDataMap,
                                  String sheetKey,
                                  ConcurrentHashMap<Integer, List<?>> sheetAndDataMap,
                                  String businessKey,
                                  ConcurrentHashMap<Integer,List<?>> otherMapData, InputStream inputStream,int mergeRowIndex,int[] mergeCollindex){
        ExcelWriter excelWriter = null;

        //需要合并的列
        //int [] mergeCollindex = {0,1,2};

        //从第一行开始
        //int mergeRowIndex = 1;

        try {
            excelWriter = EasyExcel.write(file).withTemplate(inputStream).registerWriteHandler(new MergeStrategy(mergeCollindex,mergeRowIndex)).build();
            for(Map.Entry<Integer, List<?>> entry : sheetAndDataMap.entrySet()){
                List<?> value = entry.getValue();
                Integer key = entry.getKey();

                List<?> threeData = null;
                if(otherMapData != null){
                    threeData = otherMapData.get(key);
                }

                List<?> titleData = null;
                if(titleDataMap != null){
                    titleData = titleDataMap.get(key);
                }

                WriteSheet writeSheet = EasyExcel.writerSheet(key).build();
                FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
                excelWriter.fill(new FillWrapper(sheetKey,value),fillConfig, writeSheet);

                if(threeData != null) {
                    excelWriter.fill(new FillWrapper(businessKey, threeData),fillConfig, writeSheet);
                }

                if(titleData != null) {
                    excelWriter.fill(new FillWrapper(title, titleData),fillConfig, writeSheet);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

            excelWriter.finish();

            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
