package com.szmsd.bas.controller;

import cn.hutool.core.io.IoUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author zhangyuyuan
 * @date 2021-06-29 14:38
 */
@Api(tags = {"注册协议"})
@RestController
@RequestMapping("/bas/protocol")
public class ProtocolController {
    private final Logger logger = LoggerFactory.getLogger(ProtocolController.class);

    @PreAuthorize("@ss.hasPermi('Protocol:Protocol:download')")
    @GetMapping("/download")
    @ApiOperation(value = "注册协议 - 下载文件", position = 100)
    public void download(HttpServletResponse response) {
        String defaultPath = "/u01/www/ck1/delivery/protocol";
        File dirFile = new File(defaultPath);
        if (!dirFile.exists()) {
            throw new CommonException("999", "文件夹不存在");
        }
        String[] list = dirFile.list();
        if (null == list || list.length == 0) {
            throw new CommonException("999", "文件不存在");
        }
        String downloadName = "国际物流服务合作协议-DM.docx";
        String fileName = downloadName;
        fileName = getCurrentFileName(defaultPath, fileName);
        String filePath = defaultPath + "/" + fileName;
        File file = new File(filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("File-Size", "" + list.length);
            response.setHeader("File-Name", filePath);
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setContentLengthLong(file.length());
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes("GBK"), StandardCharsets.ISO_8859_1));
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }

    @PreAuthorize("@ss.hasPermi('Protocol:Protocol:upload')")
    @PostMapping("/upload")
    @ApiOperation(value = "注册协议 - 上传文件", position = 200)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true)
    })
    public R<String> upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        // 获取文件名称
        String defaultPath = "/u01/www/ck1/delivery/protocol";
        // gei file name
        File dirFile = new File(defaultPath);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new CommonException("999", "创建文件夹失败");
            }
        }
        String fileName = "国际物流服务合作协议-DM.docx";
        fileName = getFileName(defaultPath, fileName);
        String filePath = defaultPath + "/" + fileName;
        try {
            writeFile(filePath, file.getBytes());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "保存文件失败，" + e.getMessage());
        }
        return R.ok(filePath);
    }

    @PreAuthorize("@ss.hasPermi('Protocol:Protocol:get')")
    @GetMapping("/get")
    @ApiOperation(value = "注册协议 - 查看文件", position = 300)
    public R<String[]> get(HttpServletRequest request) {
        if (null == request.getHeader("File-Get")) {
            return R.failed();
        }
        String defaultPath = "/u01/www/ck1/delivery/protocol";
        // gei file name
        File dirFile = new File(defaultPath);
        if (!dirFile.exists()) {
            return R.failed("文件夹不存在");
        }
        return R.ok(dirFile.list());
    }

    private String getCurrentFileName(String defaultPath, String fileName) {
        String originFileName = fileName;
        int index = 0;
        boolean isEnd = true;
        do {
            File f = new File(defaultPath + "/" + fileName);
            if (f.exists()) {
                index++;
            } else {
                isEnd = false;
                index--;
            }
            fileName = "国际物流服务合作协议-DM (" + index + ").docx";
        } while (isEnd);
        if (index == 0) {
            return originFileName;
        }
        return fileName;
    }

    private String getFileName(String defaultPath, String fileName) {
        int index = 0;
        boolean isEnd = true;
        do {
            File f = new File(defaultPath + "/" + fileName);
            if (f.exists()) {
                index++;
                fileName = "国际物流服务合作协议-DM (" + index + ").docx";
            } else {
                isEnd = false;
            }
        } while (isEnd);
        return fileName;
    }

    public void writeFile(String filePath, byte[] bytes) {
        BufferedOutputStream stream = null;
        try {
            stream = new BufferedOutputStream(new FileOutputStream(filePath));
            stream.write(bytes);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "保存文件异常，" + e.getMessage());
        } finally {
            if (null != stream) {
                try {
                    stream.close();
                } catch (IOException ioe) {
                    logger.error(ioe.getMessage(), ioe);
                }
            }
        }
    }
}
