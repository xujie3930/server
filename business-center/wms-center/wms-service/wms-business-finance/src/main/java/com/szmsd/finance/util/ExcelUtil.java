package com.szmsd.finance.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.szmsd.bas.api.feign.BasFileFeignService;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
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

        List<CellStyleModel> cellStyleList = new ArrayList<>();
        cellStyleList.add(CellStyleModel.createWrapTextCellStyleModel("客户资金结余", 4, 1, true));
        cellStyleList.add(CellStyleModel.createWrapTextCellStyleModel("客户资金结余", 4, 2, true));

        try {
            excelWriter = EasyExcel.write(file).withTemplate(inputStream).build();
                    //.registerWriteHandler(new MergeStrategy(mergeCollindex,mergeRowIndex))
                    //.registerWriteHandler(new CustomCellStyleHandler(cellStyleList))
                    //.build();
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
                WriteSheet writeSheet = null;
                if(key == 0) {
                    writeSheet = EasyExcel.writerSheet(key).registerWriteHandler(new MergeStrategy(mergeCollindex, mergeRowIndex)).build();
                }else{
                    writeSheet = EasyExcel.writerSheet(key).build();
                }

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
     * 使用EasyPOI导出Excel
     * @param entity 导出参数
     * @param pojoClass 数据类型
     * @param data 数据
     */
    public static void export(ExportParams entity, Class<?> pojoClass, Collection<?> data, String filepath, Integer fileId) {
        log.info("线程-【{}】-导出开始",Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        ExportParams params = new ExportParams();
        // 设置sheet得名称
        params.setSheetName("业务明细");
        ExportParams exportParams2 = new ExportParams();
        exportParams2.setSheetName("业务明细");
        // 创建sheet1使用得map
        Map<String, Object>  DelOutboundExportMap = new HashMap<>(4);
        // title的参数为ExportParams类型
        DelOutboundExportMap.put("title", params);
        // 模版导出对应得实体类型
        DelOutboundExportMap.put("entity", pojoClass);
        // sheet中要填充得数据
        DelOutboundExportMap.put("data", data);
        // 创建sheet2使用得map
        // 将sheet1和sheet2使用得map进行包装
        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(DelOutboundExportMap);

        Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
        Sheet sheet= workbook.getSheet("业务明细");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<26;i++){
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();

            styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());


            Font font = workbook.createFont();
            //true为加粗，默认为不加粗
            font.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain.setFont(font);

            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain.setAlignment(HorizontalAlignment.CENTER);
            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);

            deliveryTimeCell.setCellStyle(styleMain);
        }

        Sheet sheet1= workbook.getSheet("业务明细");
        //获取第一行数据
        //Row row3 =sheet1.getRow(0);

//        for (int i=0;i<7;i++){
//            Cell deliveryTimeCell = row3.getCell(i);
//
//            CellStyle styleMain = workbook.createCellStyle();
//
//            styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
//
//
//            Font font = workbook.createFont();
//            //true为加粗，默认为不加粗
//            font.setBold(true);
//            //设置字体颜色，颜色和上述的颜色对照表是一样的
//            font.setColor(IndexedColors.WHITE.getIndex());
//            //将字体样式设置到单元格样式中
//            styleMain.setFont(font);
//
//            styleMain.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//            styleMain.setAlignment(HorizontalAlignment.CENTER);
//            styleMain.setVerticalAlignment(VerticalAlignment.CENTER);
//
//            deliveryTimeCell.setCellStyle(styleMain);
//        }
        if (workbook != null){

            downLoadExcel(entity.getTitle(), workbook,filepath,fileId);
        }

        log.info("线程-【{}】-导出结束-耗时:{}ms",Thread.currentThread().getName(), System.currentTimeMillis() - start);
    }


    /**
     * 下载Excel
     * @param fileName 文件名
     * @param workbook Excel对象
     */
    public static void downLoadExcel(String fileName, Workbook workbook,String filepath,Integer fileId) {
        if (workbook instanceof HSSFWorkbook) {
            fileName = fileName + ".xls";
        } else {
            fileName = fileName + ".xlsx";
        }
        File excelFile = new File(filepath + fileName);
        if(!excelFile.getParentFile().exists()) {
            excelFile.getParentFile().mkdirs();
        }
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(excelFile);
            workbook.write(outputStream);
            DecimalFormat df= new DecimalFormat("0.00");
            String fileSize = df.format((double) excelFile.length() / 1048576);
            outputStream.flush();
            updateBasfile(fileSize,fileId);


        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            if (outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 修改文件大小值
     * @param fileSize 文件大小
     * @param fileId 文件id
     */
    public static void updateBasfile(String fileSize, Integer fileId) {
        BasFileFeignService basFileFeignService= SpringUtils.getBean(BasFileFeignService.class);
        BasFile basFile = new BasFile();
        basFile.setId(fileId);
        basFile.setFileSize(fileSize+"MB");
        basFileFeignService.updatebasFile(basFile);
    }




    /**
     * 创建sheet(适合大数据量)
     * @param sheetName sheet名称
     * @param pojoClass 数据类型
     * @param data 数据
     * @param workbook 工作簿
     */
    public static void createSheet(String sheetName, Class<?> pojoClass, Collection<?> data, SXSSFWorkbook workbook) {
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        Iterator<?> iterator = data.iterator();
        Field[] fields = pojoClass.getDeclaredFields();
        int rowNum = 0;
        while (iterator.hasNext()){
            Object item = iterator.next();
            Row row = sheet.createRow(rowNum);
            for (int i = 0; i < fields.length; i++) {
                Cell cell = row.createCell(i);
                if (rowNum == 0){
                    cell.setCellValue(fields[i].getName());
                }else {
                    try {
                        fields[i].setAccessible(true);
                        cell.setCellValue(String.valueOf(fields[i].get(item)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            rowNum++;
        }
    }
}
