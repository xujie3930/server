package com.szmsd.finance.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jiangjun
 * @version 1.0
 * @description:解析excel数据
 * @date 2022/7/27 11:15
 */
public class ExcelFile {

    /**
     * @Author
     * @Description //:解析excel数据
     * @Date 2022/7/27 11:15
     * @Param file ：上传的excel文件
     * @return  * @param null
     */
    public static List<Map> getExcelData(MultipartFile file,List<String> list1) throws IOException {
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<Map> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                //循环除了所有行,如果要循环除第一行以外的就firstRowNum+1
                for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {

                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    if (lastCellNum!=list1.size()) {
                        return list;
                    }

                    if (lastCellNum > 0){
                        ArrayList<Object> cellValues = new ArrayList<>();
                        //循环当前行
                        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                               Cell cell = row.getCell(cellNum);
                               cellValues.add(getCellValue(cell));

                        }
                        Map map=new HashMap();
                       for(int i =0; i < list1.size(); i++){
                            map.put(list1.get(i),cellValues.get(i));
                         }
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }

    public static List<Map> getExcelData2(MultipartFile file,List<String> list1) throws IOException {
        //检查文件
        checkFile(file);
        //获得Workbook工作薄对象
        Workbook workbook = getWorkBook(file);
        //创建返回对象，把每行中的值作为一个数组，所有行作为一个集合返回
        List<Map> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前sheet的开始行
                int firstRowNum = sheet.getFirstRowNum();
                //获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();

                //循环除了所有行,如果要循环除第一行以外的就firstRowNum+1
                for (int rowNum = firstRowNum + 2; rowNum <= lastRowNum; rowNum++) {
                    //获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null){
                        continue;
                    }
                    //获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    //获得当前行的列数
                    int lastCellNum = row.getLastCellNum();
                    if (lastCellNum!=list1.size()+1) {
                        return list;
                    }
                    if (lastCellNum > 0){
                        ArrayList<Object> cellValues = new ArrayList<>();
                        //循环当前行
                        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                            Cell cell = row.getCell(cellNum);
                            cellValues.add(getCellValue(cell));
                        }
                        Map map=new HashMap();
                        for (int i = 0; i < list1.size(); i++) {
                            map.put(list1.get(i),cellValues.get(i));
                        }
                        list.add(map);
                    }
                }
            }
        }
        return list;
    }
    /**
     * 检查文件
     *
     * @param file
     * @throws IOException
     */
    public static void checkFile(MultipartFile file) throws IOException {
        //判断文件是否存在
        if (null == file) {
            System.err.println("文件不存在！");
        }
        //获得文件名
        String fileName = file.getOriginalFilename();
        //判断文件是否是excel文件
        if (!fileName.endsWith("xls") && !fileName.endsWith("xlsx")) {
            System.err.println("不是excel文件");
        }
    }

    public static Workbook getWorkBook(MultipartFile file) {
        //获得文件名
        String fileName = file.getOriginalFilename();
        //创建Workbook工作薄对象，表示整个excel
        Workbook workbook = null;
        try {
            //获取excel文件的io流
            InputStream is = file.getInputStream();
            //根据文件后缀名不同(xls和xlsx)获得不同的Workbook实现类对象
            if (fileName.endsWith("xls")) {
                //2003
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith("xlsx")) {
                //2007 及2007以上
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return workbook;
    }

    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        //判断数据的类型
        //判断数据的类型
        switch (cell.getCellTypeEnum()) {
            case NUMERIC: //数字
                cellValue = stringDateProcess(cell);
                break;
            case STRING: //字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN: //Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA: //公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK: //空值
                cellValue = "";
                break;
            case ERROR: //故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }


    public static String stringDateProcess(Cell cell) {
        String result = new String();
        if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
            SimpleDateFormat sdf = null;
            if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                sdf = new SimpleDateFormat("HH:mm");
            } else {// 日期
                sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            }
            Date date = cell.getDateCellValue();
            result = sdf.format(date);
        } else if (cell.getCellStyle().getDataFormat() == 58) {
            // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            double value = cell.getNumericCellValue();
            Date date = DateUtil
                    .getJavaDate(value);
            result = sdf.format(date);
        } else {
            double value = cell.getNumericCellValue();
            CellStyle style = cell.getCellStyle();
            DecimalFormat format = new DecimalFormat();
            String temp = style.getDataFormatString();
            // 单元格设置成常规
            if (temp.equals("General")) {
                format.applyPattern("#");
            }
            result = format.format(value);
        }

        return result;
    }

    public static InputStream convertorStream(Workbook workbook) {
        InputStream in = null;
        try {
            //临时缓冲区
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //创建临时文件
            workbook.write(out);
            byte[] bookByteAry = out.toByteArray();
            in = new ByteArrayInputStream(bookByteAry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return in;
    }

    //本地数据库转格式(转成MultipartFile然后在计算他的总列数)
    public static int ExcleTotalColumn(String path,String fileName){
        int coloumNum1=0;
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "textField";
        FileItem item = factory.createItem(textFieldName, "text/plain", true, fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(path+fileName);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MultipartFile multipartFile = new CommonsMultipartFile(item);
        Workbook wb1 = ExcelFile.getWorkBook(multipartFile);

        if (wb1 != null) {
            for (int sheetNum = 0; sheetNum < wb1.getNumberOfSheets(); sheetNum++) {
                //获得当前sheet工作表
                Sheet sheet = wb1.getSheetAt(sheetNum);
                //获得总列数
                coloumNum1=sheet.getRow(0).getPhysicalNumberOfCells();
                //rowNum=sheet.getLastRowNum();//获得总行数
            }
        }
        return  coloumNum1;
    }

}
