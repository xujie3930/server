package com.szmsd.delivery.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.mapper.DelBasFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 导出工具类
 *
 * @author jun
 */
@Slf4j
public class ExcelUtil {
    /**
     * 导出路径
     */

    //public static String filePath;
    /**
     * xlsx格式文件最大行数
     */
    public static Integer SHEET_DATA_MAX_LIMIT_XLSX = 2 << 19;
    /**
     * xls格式文件最大行数
     */
    public static Integer SHEET_DATA_MAX_LIMIT_XLS = 2 << 15;

//    @Autowired
//    private BasFileMapper basFileMapper;


    /**
     * 下载Excel
     * @param fileName 文件名
     * @param workbook Excel对象
     */
    public static void downLoadExcel(String fileName, Workbook workbook,String filepath,Integer fileId) {
        DelBasFileMapper basFileMapper= SpringUtils.getBean(DelBasFileMapper.class);
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
            BasFile basFile = new BasFile();
            basFile.setId(fileId);
            basFile.setFileSize(fileSize+"MB");
            basFileMapper.updateByPrimaryKeySelective(basFile);
            outputStream.flush();

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
     * 使用EasyPOI导出Excel
     * @param entity 导出参数
     * @param pojoClass 数据类型
     * @param data 数据
     */
    public static void export(ExportParams entity, Class<?> pojoClass, Collection<?> data,Collection<?> data2,Class<?> pojoClass2,String filepath,Integer fileId) {
        log.info("线程-【{}】-导出开始",Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        ExportParams params = new ExportParams();
        // 设置sheet得名称
        params.setSheetName("出库单详情");
        ExportParams exportParams2 = new ExportParams();
        exportParams2.setSheetName("包裹明细");
        // 创建sheet1使用得map
        Map<String, Object>  DelOutboundExportMap = new HashMap<>(4);
        // title的参数为ExportParams类型
        DelOutboundExportMap.put("title", params);
        // 模版导出对应得实体类型
        DelOutboundExportMap.put("entity", pojoClass);
        // sheet中要填充得数据
        DelOutboundExportMap.put("data", data);
        // 创建sheet2使用得map
        Map<String, Object>  DelOutboundExportItemListMap = new HashMap<>(4);
        DelOutboundExportItemListMap.put("title", exportParams2);
        DelOutboundExportItemListMap.put("entity", pojoClass2);
        DelOutboundExportItemListMap.put("data", data2);
        // 将sheet1和sheet2使用得map进行包装
        List<Map<String, Object>> sheetsList = new ArrayList<>();
        sheetsList.add(DelOutboundExportMap);
        sheetsList.add(DelOutboundExportItemListMap);

        Workbook workbook = ExcelExportUtil.exportExcel(sheetsList, ExcelType.HSSF);
        Sheet sheet= workbook.getSheet("出库单详情");

        //获取第一行数据
        Row row2 =sheet.getRow(0);

        for (int i=0;i<32;i++){
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

        Sheet sheet1= workbook.getSheet("包裹明细");
        //获取第一行数据
        Row row3 =sheet1.getRow(0);

        for (int i=0;i<7;i++){
            Cell deliveryTimeCell = row3.getCell(i);

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
        if (workbook != null){



            downLoadExcel(entity.getTitle(), workbook,filepath,fileId);
        }

        log.info("线程-【{}】-导出结束-耗时:{}ms",Thread.currentThread().getName(), System.currentTimeMillis() - start);
    }



    /**
     * 使用POI导出Excel
     * @param exParams 导出参数
     * @param pojoClass 数据类型
     * @param data 数据
     */
    public static void exportBySxssf(ExParams exParams, Class<?> pojoClass, Collection<?> data,Collection<?> data2,Class<?> pojoClass2,String filepath,Integer fileId) {
        log.info("线程-【{}】-导出开始",Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        SXSSFWorkbook workbook = new SXSSFWorkbook(2<<10);
        if (exParams.getIfOpenMultiSheet() && (data instanceof List || data instanceof Set)){
            if (exParams.getDataNumsOfSheet() == null || exParams.getDataNumsOfSheet() <= 0){
                throw new RuntimeException("单页导出数据量不合法");
            }
            if (exParams.getDataNumsOfSheet() > SHEET_DATA_MAX_LIMIT_XLSX){
                throw new RuntimeException("单页导出数据量过大");
            }
            List<?> list = new ArrayList<>(data);
            int sheetPageNum = list.size() % exParams.getDataNumsOfSheet() == 0 ? list.size() / exParams.getDataNumsOfSheet() : list.size() / exParams.getDataNumsOfSheet() + 1;
            for (int i = 0; i < sheetPageNum; i++) {
                createSheet(exParams.getSheetName()+"("+(i* exParams.getDataNumsOfSheet())+"-"+(Math.min(list.size(), (i + 1) * exParams.getDataNumsOfSheet()))+")"
                    , pojoClass
                    , list.subList(i* exParams.getDataNumsOfSheet(), Math.min(list.size(), (i + 1) * exParams.getDataNumsOfSheet()))
                    , workbook);
            }
        }else {
            createSheet(exParams.getSheetName(), pojoClass, data, workbook);
        }
        downLoadExcel(exParams.getFileName(), workbook,filepath,fileId);
        log.info("线程-【{}】-导出结束-耗时:{}ms",Thread.currentThread().getName(), System.currentTimeMillis() - start);
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
