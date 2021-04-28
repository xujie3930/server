package com.szmsd.putinstorage.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * @author MSD
 * @date 2021-02-23
 */
public class ExcelUtil {

    protected static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * @param excel
     * @param row
     * @param oneRowBold
     * @param j
     */
    public static void bord(Workbook excel, Row row, boolean oneRowBold, int j) {
        CellStyle cellStyle = excel.createCellStyle();

        //设置自动换行
        cellStyle.setWrapText(true);

        // 边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);

        // 居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 字体
        Font font = excel.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        // 首行标题加粗
        if (oneRowBold) {
            font.setFontHeightInPoints((short) 14);
            font.setFontName("黑体");
            font.setBold(true);
        }
        cellStyle.setFont(font);

        for (int i = 0; i <= j; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                cell.setCellStyle(cellStyle);
            }
        }
    }

    /**
     * 插入图片
     *
     * @param excel
     * @param sheet
     * @param row
     * @param pathUrl
     * @throws Exception
     */
    public static void insertImage(Workbook excel, Sheet sheet, int row, int col, String pathUrl) throws Exception {
        InputStream is = new FileInputStream(pathUrl);
        insertImage(excel, sheet, row, col, is);
    }

    /**
     * 插入图片 不建议使用URL 如果文件不存在或者pathUrl不是规则路径则速度会非常慢
     * @param excel
     * @param sheet
     * @param row
     * @throws Exception
     */
    @Deprecated
    public static void insertImage(Workbook excel, Sheet sheet, int row, int col, URL url) throws Exception {
        InputStream is = url.openStream();
        insertImage(excel, sheet, row, col, is);
    }


    public static void insertImage(Workbook excel, Sheet sheet, int row, int col, InputStream is) throws Exception {
        CreationHelper helper = excel.getCreationHelper();
        Drawing drawing = sheet.createDrawingPatriarch();
        ClientAnchor anchor = helper.createClientAnchor();
        // 图片插入坐标
        anchor.setRow1(row);
        anchor.setCol1(col);
        byte[] bytes = IOUtils.toByteArray(is);
        int pictureIdx = excel.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
        Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize(1, 1);

    }
}
