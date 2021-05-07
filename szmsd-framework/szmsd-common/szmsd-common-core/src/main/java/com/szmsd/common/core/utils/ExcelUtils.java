package com.szmsd.common.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
        setHeaderInformation(response, exportExcel.excelName(), exportExcel.headerInformation());
        ExportContext exportContext = () -> condition;
        ExportSheet<?>[] sheets = exportExcel.sheets();
        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).build();
        for (ExportSheet<?> sheet : sheets) {
            WriteSheet writeSheet = EasyExcel.writerSheet(sheet.sheetName()).head(sheet.classType()).build();
            QueryPage<?> queryPage = sheet.query(exportContext);
            boolean isWrite = false;
            for (; ; ) {
                Page<?> pageInfo = queryPage.getPage();
                long total = pageInfo.getTotal();
                // 判断有没有数据
                if (total > 0) {
                    // 批量处理数据
                    // 这里做一层数据的转换，导出另外写一个dto，分页的dto字段太多了，避免个性化处理
                    excelWriter.write(BeanMapperUtil.mapList(pageInfo, sheet.classType()), writeSheet);
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
        }
        excelWriter.finish();
    }

    /**
     * 构建导出对象
     *
     * @param excelName         excelName
     * @param headerInformation headerInformation
     * @param sheets            sheets
     * @return ExportExcel
     */
    public static ExportExcel build(final String excelName, final Map<String, String> headerInformation, final ExportSheet<?>... sheets) {
        return new ExportExcel() {
            @Override
            public String excelName() {
                return excelName;
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
     * 导出上下文
     */
    public interface ExportContext {

        Object getCondition();
    }

    /**
     * 导出excel
     */
    public interface ExportExcel {

        static ExportExcel build(final String excelName, final Map<String, String> headerInformation, final ExportSheet<?>... sheets) {
            return new ExportExcel() {
                @Override
                public String excelName() {
                    return excelName;
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
