package com.szmsd.finance.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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
        ExcelWriter excelWriter = null;
        try {
            OutputStream outputStream = response.getOutputStream();
            response.setHeader("Content-disposition", "attachment; filename=" + filename + ".xlsx");
            response.setContentType("application/vnd.ms-excel;charset=UTF-8");//设置类型
            response.setHeader("Pragma", "No-cache");//设置头
            response.setHeader("Cache-Control", "no-cache");//设置头
            response.setDateHeader("Expires", 0);//设置日期头
            excelWriter = EasyExcel.write(outputStream).withTemplate(inputStream).build();
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
