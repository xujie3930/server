package com.szmsd.common.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.github.pagehelper.Page;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 19:18
 */
public class ExcelUtils {

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
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("UTF-8");
        excelName = URLEncoder.encode(excelName, "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + excelName + ExcelTypeEnum.XLSX.getValue() + "");
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
}
