package com.szmsd.exception.task;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.exception.dto.ExceptionInfoExportCustomerDto;
import com.szmsd.exception.dto.ExceptionInfoExportDto;
import com.szmsd.exception.dto.ExceptionInfoQueryDto;
import com.szmsd.exception.mapper.BasExcetionFileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 导出工具类
 *
 * @author jun
 */
@Slf4j
public class ExceptionExcelUtil {
    /**
     * 导出路径
     */

    /**
     * xlsx格式文件最大行数
     */
    public static Integer SHEET_DATA_MAX_LIMIT_XLSX = 2 << 19;
    /**
     * xls格式文件最大行数
     */
    public static Integer SHEET_DATA_MAX_LIMIT_XLS = 2 << 15;



    /**
     * 下载Excel
     * @param fileName 文件名
     * @param workbook Excel对象
     */
    public static void downLoadExcel(String fileName, Workbook workbook,String filepath,Integer fileId) {
        BasExcetionFileMapper basFileMapper= SpringUtils.getBean(BasExcetionFileMapper.class);
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
    public static void export(ExportParams entity, Class<?> pojoClass, Collection<?> data, String filepath, Integer fileId, ExceptionInfoQueryDto exceptionInfoQueryDto) {
        log.info("线程-【{}】-导出开始",Thread.currentThread().getName());
        long start = System.currentTimeMillis();
        ExportParams params = new ExportParams();
        int a = 0;
        Workbook workbook = null;
        if (exceptionInfoQueryDto.getType() == 0) {
            List<ExceptionInfoExportCustomerDto> exceptionInfoExportCustomerDtos = BeanMapperUtil.mapList(data, ExceptionInfoExportCustomerDto.class);
            workbook = ExcelExportUtil.exportExcel(params, ExceptionInfoExportCustomerDto.class, exceptionInfoExportCustomerDtos);
            a = 1;

        } else if (exceptionInfoQueryDto.getType() == 1) {
            workbook = ExcelExportUtil.exportExcel(params, ExceptionInfoExportDto.class, data);

        }
        Sheet sheet = workbook.getSheet("sheet0");
        //获取第一行数据
        Row row2 = sheet.getRow(0);

        for (int i = 0; i < 19 - a; i++) {
            Cell deliveryTimeCell = row2.getCell(i);

            CellStyle styleMain = workbook.createCellStyle();
            if (i == 18 - a) {
                styleMain.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
            } else {
                styleMain.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());

            }
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
//        CellStyle style =  workbook.createCellStyle();
//        style.setFillPattern(HSSFColor.HSSFColorPredefined.valueOf(""));
//        style.setFillForegroundColor(IndexedColors.RED.getIndex());
            deliveryTimeCell.setCellStyle(styleMain);
        }

        //获取第二行数据
        Row row3 = sheet.getRow(1);
        for (int x = 18 - a; x < 23 - a; x++) {

            Cell deliveryTimeCell1 = row3.getCell(x);
            CellStyle styleMain1 = workbook.createCellStyle();
            styleMain1.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            Font font1 = workbook.createFont();
            //true为加粗，默认为不加粗
            font1.setBold(true);
            //设置字体颜色，颜色和上述的颜色对照表是一样的
            font1.setColor(IndexedColors.WHITE.getIndex());
            //将字体样式设置到单元格样式中
            styleMain1.setFont(font1);


            styleMain1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleMain1.setAlignment(HorizontalAlignment.CENTER);
            styleMain1.setVerticalAlignment(VerticalAlignment.CENTER);

            deliveryTimeCell1.setCellStyle(styleMain1);
        }

        if (exceptionInfoQueryDto.getType() == 1) {
            sheet.setColumnHidden(18, true);
        } else {
            sheet.setColumnHidden(17, true);

        }


        if (workbook != null){



            downLoadExcel(entity.getTitle(), workbook,filepath,fileId);
        }

        log.info("线程-【{}】-导出结束-耗时:{}ms",Thread.currentThread().getName(), System.currentTimeMillis() - start);
    }





}
