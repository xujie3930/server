package com.szmsd.bas.controller;

import cn.hutool.core.io.IoUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author zhangyuyuan
 * @date 2021-06-29 14:38
 */
@Api(tags = {"注册协议"})
@RestController
@RequestMapping("/bas/protocol")
public class ProtocolController {
    private final Logger logger = LoggerFactory.getLogger(ProtocolController.class);
    private final String defaultPath = "/u01/www/ck1/delivery/protocol";

    @PreAuthorize("@ss.hasPermi('Protocol:Protocol:download')")
    @GetMapping("/download")
    @ApiOperation(value = "注册协议 - 下载文件", position = 100)
    public void download(HttpServletRequest request, HttpServletResponse response) {
        String path = buildDefaultPath(request);
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            throw new CommonException("999", "文件夹不存在");
        }
        String[] list = dirFile.list();
        if (null == list || list.length == 0) {
            throw new CommonException("999", "文件不存在");
        }
        File file = getDownloadFile(path);
        if (null == file) {
            throw new CommonException("999", "文件不存在");
        }
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            response.setHeader("File-Size", "" + list.length);
            response.setHeader("File-Name", file.getPath());
            inputStream = new FileInputStream(file);
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setContentLengthLong(file.length());
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String efn = URLEncoder.encode(unWrapperFileName(file), "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn);
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
            @ApiImplicitParam(paramType = "form", dataType = "__file", name = "file", value = "上传文件", required = true, allowMultiple = true),
            @ApiImplicitParam(paramType = "form", dataType = "String", name = "langr", value = "语言", example = "zh")
    })
    public R<String> upload(HttpServletRequest request, @RequestParam("langr") String langr) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        AssertUtil.notNull(file, "上传文件不存在");
        if (StringUtils.isEmpty(langr)) {
            langr = request.getHeader("Langr");
            if (StringUtils.isEmpty(langr)) {
                langr = "zh";
            }
        }
        String path = buildDefaultPath(langr);
        // gei file name
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                throw new CommonException("999", "创建文件夹失败");
            }
        }
        // 获取上传文件的名称（下载的时候名字）
        // 如果文件名字相同
        // 该文件如果已存在，又上传了一个名字是<文档.doc>的文件。则新的文件后面添加序号
        // （括号的规则，原文件名(不包含后缀) + 空格 + "(" + 下标 + ")"）
        // 文档.doc
        // 文档 (1).doc
        // 文档 (2).doc
        // 文档 (3).doc
        String fileName = file.getOriginalFilename();
        if (null == fileName || "".equals(fileName.trim())) {
            throw new CommonException("999", "源文件名不能为空");
        }
        fileName = getFileName(path, fileName);
        String filePath = path + "/" + fileName;
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
        String path = buildDefaultPath(request);
        // gei file name
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            return R.failed("文件夹不存在");
        }
        return R.ok(dirFile.list());
    }

    private String buildDefaultPath(HttpServletRequest request) {
        String langr = this.getLangr(request);
        return this.buildDefaultPath(langr);
    }

    private String buildDefaultPath(String langr) {
        String path;
        if ("zh".equals(langr)) {
            path = defaultPath;
        } else {
            path = defaultPath + "/" + langr;
        }
        return path;
    }

    private String getLangr(HttpServletRequest request) {
        return this.getLangr(request.getHeader("Langr-Force"), request);
    }

    private String getLangr(String langr, HttpServletRequest request) {
        if (StringUtils.isEmpty(langr)) {
            // 获取国际化
            langr = request.getHeader("Langr");
            if (StringUtils.isEmpty(langr)) {
                langr = "zh";
            }
        }
        return langr;
    }

    private File getDownloadFile(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (null != listFiles && listFiles.length > 0) {
                long max = 0;
                File maxFile = null;
                for (File listFile : listFiles) {
                    if (listFile.isDirectory()) {
                        continue;
                    }
                    if (max < listFile.lastModified()) {
                        max = listFile.lastModified();
                        maxFile = listFile;
                    }
                }
                return maxFile;
            }
        }
        return null;
    }

    private String unWrapperFileName(File file) {
        String name = file.getName();
        String reg = " \\([0-9]+\\)";
        name = name.replaceAll(reg, "");
        return name;
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
        int lastIndexOf = fileName.lastIndexOf(".");
        String name;
        String extName;
        if (lastIndexOf != -1) {
            name = fileName.substring(0, lastIndexOf);
            extName = fileName.substring(lastIndexOf + 1);
        } else {
            name = fileName;
            extName = "";
        }
        int index = 0;
        boolean isEnd = true;
        do {
            File f = new File(defaultPath + "/" + fileName);
            if (f.exists()) {
                index++;
                fileName = name + " (" + index + ")." + extName;
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
