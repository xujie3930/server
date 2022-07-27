package com.szmsd.finance.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * @ClassName: DownloadTemplateUtil
 * @Description:
 * @Author: 11
 * @Date: 2021-12-03 9:52
 */
@Slf4j
public final class DownloadTemplateUtil {

    private static DownloadTemplateUtil downloadTemplateUtil;

    public static DownloadTemplateUtil getInstance() {
        if (Objects.isNull(downloadTemplateUtil)) {
            synchronized (DownloadTemplateUtil.class) {
                if (Objects.isNull(downloadTemplateUtil)) {
                    DownloadTemplateUtil.downloadTemplateUtil = new DownloadTemplateUtil();
                }
            }
        }
        return DownloadTemplateUtil.downloadTemplateUtil;
    }

    private DownloadTemplateUtil() {
    }

    /**
     * 获取resource下的xlsx文件
     *
     * @param httpServletResponse
     * @param fileName            文件名
     */
    public void getResourceByName(HttpServletResponse httpServletResponse, String fileName) {
        getTemplate(httpServletResponse, fileName, "/template/", "xls");
    }

    public void getTemplate(HttpServletResponse response, String fileName, String filePath, String ext) {
        String fileOriginName = fileName + "." + ext;
        Resource resource = new ClassPathResource(filePath + fileOriginName);
        if (!resource.exists()) throw new RuntimeException(fileOriginName + "文件不存在");

        try (InputStream inputStream = resource.getInputStream();
             ServletOutputStream outputStream = response.getOutputStream()
        ) {
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
//            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
