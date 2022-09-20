package com.szmsd.delivery.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@SuppressWarnings("all")
public class ZipFileUtils {


    /**
     * 下载ZIP压缩包
     *
     * @param file     zip压缩包文件
     * @param response 响应
     * @author liukai
     */
    public static void downloadZip(HttpServletResponse response, String filePath, String fileName) {

        // 先去模板目录中获取模板
        // 模板目录中没有模板再从项目中获取模板
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                response.setHeader("File-Source", "local");
            } else {
                Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/octet-stream");
            String efn = URLEncoder.encode(fileName, "utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + ".zip");
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();;
            throw new CommonException("999", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();;
            throw new CommonException("999", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }

    }

    /**
     * 任何单文件下载
     *
     * @param file     要下载的文件
     * @param response 响应
     * @author liukai
     */
    public static void downloadAnyFile(File file, HttpServletResponse response) {
        FileInputStream fileInputStream = null;
        OutputStream outputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            // 清空response
            response.reset();
            //防止文件名中文乱码
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(),"UTF-8"));
            //根据文件动态setContentType
            response.setContentType(new MimetypesFileTypeMap().getContentType(file) + ";charset=UTF-8");
            outputStream = response.getOutputStream();
            byte[] bytes = new byte[2048];
            int len;
            while ((len = fileInputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
