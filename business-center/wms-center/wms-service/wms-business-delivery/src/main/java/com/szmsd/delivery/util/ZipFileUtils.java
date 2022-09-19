package com.szmsd.delivery.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ZipUtil;
import com.szmsd.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.activation.MimetypesFileTypeMap;
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
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);

        OutputStream toClient = null;
        try {
            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            toClient.write(buffer);
            toClient.flush();
        } catch (Exception e) {
            log.error("下载zip压缩包过程发生异常:", e);
        } finally {
            if (toClient != null) {
                try {
                    toClient.close();
                } catch (IOException e) {
                    log.error("zip包下载关流失败:", e);
                }
            }
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
