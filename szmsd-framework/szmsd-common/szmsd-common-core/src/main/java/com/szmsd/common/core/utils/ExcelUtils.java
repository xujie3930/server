package com.szmsd.common.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 19:18
 */
public class ExcelUtils {

    /**
     * 设置头部信息
     *
     * @param response  response
     * @param excelName excelName
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    private static void setHeaderInformation(HttpServletResponse response, String excelName) throws UnsupportedEncodingException {
        setHeaderInformation(response, excelName, null);
    }

    /**
     * 设置头部信息
     *
     * @param response       response
     * @param excelName      excelName
     * @param otherHeaderMap otherHeaderMap
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     */
    private static void setHeaderInformation(HttpServletResponse response, String excelName, Map<String, String> otherHeaderMap) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        excelName = URLEncoder.encode(excelName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue() + "");
        if (null != otherHeaderMap && !otherHeaderMap.isEmpty()) {
            otherHeaderMap.forEach(response::setHeader);
        }
    }

    /**
     * 分页模式导出
     *
     * @param response  响应
     * @param excelName Excel名称
     * @param sheetName sheet页名称
     * @param clazz     Excel要转换的类型
     * @param queryPage 分页实现
     * @throws Exception e
     */
    public static void export2WebPage(HttpServletResponse response, String excelName, String sheetName, Class<?> clazz, QueryPage<?> queryPage) throws Exception {
        setHeaderInformation(response, excelName);
        // 分页模式导出
        // 写入输出流
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream(), clazz).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).build();
        boolean isWrite = false;
        // 分页导出所有的数据
        for (; ; ) {
            Page<?> pageInfo = queryPage.getPage();
            long total = pageInfo.getTotal();
            // 判断有没有数据
            if (total > 0) {
                // 批量处理数据
                // 这里做一层数据的转换，导出另外写一个dto，分页的dto字段太多了，避免个性化处理
                excelWriter.write(BeanMapperUtil.mapList(pageInfo, clazz), writeSheet);
                // 标记写过数据
                isWrite = true;
            }
            // 处理数据
            if (pageInfo.getPages() <= pageInfo.getPageNum()) {
                // 当前页数大于等于总页数，结束循环
                break;
            }
            // 下一页
            queryPage.nextPage();
        }
        // 处理空excel的问题
        if (!isWrite) {
            // 写一个空行
            excelWriter.write(new ArrayList<>(), writeSheet);
        }
        // 关闭流
        excelWriter.finish();
    }

    /**
     * 导出
     *
     * @param response    response
     * @param condition   condition
     * @param exportExcel exportExcel
     * @throws Exception e
     */
    public static void export(HttpServletResponse response, Object condition, ExportExcel exportExcel) throws Exception {
        export(response, null, condition, exportExcel);
    }


    private static List<List<String>> myHead(Class clazz, String len){
        List<List<String>> root = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            // 除过fieldMap中的属性，其他属性都获取
            boolean annotationPresent = fields[i].isAnnotationPresent(ExcelProperty.class);
            if (annotationPresent) {
                // 获取注解值
                String[] name = fields[i].getAnnotation(ExcelProperty.class).value();
                if("en".equals(len)){
                    if(name != null && name.length > 1){
                        root.add(Arrays.asList(name[1]));
                    }
                }else{
                    root.add(Arrays.asList(name[0]));
                }

            }
        }
        return root;
    }

    /**
     * 导出
     *
     * @param response    response
     * @param inputStream inputStream
     * @param condition   condition
     * @param exportExcel exportExcel
     * @throws Exception e
     */
    public static void export(HttpServletResponse response, InputStream inputStream, Object condition, ExportExcel exportExcel) throws Exception {
        setHeaderInformation(response, exportExcel.excelName(), exportExcel.headerInformation());
        ExportContext exportContext = () -> condition;
        ExportSheet<?>[] sheets = exportExcel.sheets();
        ExcelWriter excelWriter;
        if (null != inputStream) {
            excelWriter = EasyExcel.write(response.getOutputStream()).withTemplate(inputStream).build();
        } else {
            excelWriter = EasyExcel.write(response.getOutputStream()).build();
        }
        int sheetNo = 0;
        for (ExportSheet<?> sheet : sheets) {
            WriteSheet writeSheet = null;
            if(StringUtils.isNotEmpty(exportExcel.len())){
                writeSheet = EasyExcel.writerSheet(sheetNo).head(myHead(sheet.classType(), exportExcel.len())).build();
            }else{
                writeSheet = EasyExcel.writerSheet(sheetNo).head(sheet.classType()).build();
            }
            writeSheet.setSheetName(sheet.sheetName());
            QueryPage<?> queryPage = sheet.query(exportContext);
            boolean isWrite = false;
            for (; ; ) {
                Page<?> pageInfo = queryPage.getPage();
                long total = pageInfo.getTotal();
                // 判断有没有数据
                if (total > 0) {
                    // 批量处理数据
                    // 这里做一层数据的转换，导出另外写一个dto，分页的dto字段太多了，避免个性化处理
                    if (null != inputStream) {
                        excelWriter.fill(pageInfo, writeSheet);
                    } else {
                        excelWriter.write(BeanMapperUtil.mapList(pageInfo, sheet.classType()), writeSheet);
                    }
                    // 标记写过数据
                    isWrite = true;
                }
                // 处理数据
                if (pageInfo.getPages() <= pageInfo.getPageNum()) {
                    // 当前页数大于等于总页数，结束循环
                    break;
                }
                // 下一页
                queryPage.nextPage();
            }
            // 处理空excel的问题
            if (!isWrite) {
                // 写一个空行
                if (null != inputStream) {
                    excelWriter.fill(new ArrayList<>(), writeSheet);
                } else {
                    excelWriter.write(new ArrayList<>(), writeSheet);
                }
            }
            sheetNo++;
        }
        excelWriter.finish();
    }


    /**
     * 导出上下文
     */
    public interface ExportContext {

        Object getCondition();
    }

    /**
     * 导出excel
     */
    public interface ExportExcel {

        static ExportExcel build(final String excelName, String len, final Map<String, String> headerInformation, final ExportSheet<?>... sheets) {
            return new ExportExcel() {
                @Override
                public String excelName() {
                    return excelName;
                }

                @Override
                public String len() {
                    return len;
                }

                @Override
                public Map<String, String> headerInformation() {
                    return headerInformation;
                }

                @Override
                public ExportSheet<?>[] sheets() {
                    return sheets;
                }
            };
        }

        /**
         * 导出文件名称，包括后缀
         *
         * @return String
         */
        String excelName();

        String len();


        /**
         * 设置响应头信息
         *
         * @return Map<String, String>
         */
        Map<String, String> headerInformation();

        /**
         * 导出的sheet
         *
         * @return ExportSheet<?>[]
         */
        ExportSheet<?>[] sheets();
    }

    /**
     * 导出sheet
     */
    public interface ExportSheet<T> {

        /**
         * sheet名称
         *
         * @return String
         */
        String sheetName();

        /**
         * 导出对象
         *
         * @return Class<T>
         */
        Class<T> classType();

        /**
         * 查询导出数据
         *
         * @param exportContext exportContext
         * @return QueryPage<T>
         */
        QueryPage<T> query(ExportContext exportContext);
    }
}
