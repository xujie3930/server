package com.szmsd.bas.util;


import com.szmsd.bas.domain.dto.FileDTO;
import com.szmsd.bas.enums.BasAttachmentTypeEnum;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringToolkit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

@Slf4j
public class FileUtil {

    private static Pattern linePattern = Pattern.compile("_(\\w)");

    /**
     * 请先在nacos中配置各个模块文件路径已经访问路径
     *
     * @param fileGen
     * @return
     */
    public static Files getFileUrl(FileDTO fileGen) {
        if (null == fileGen) {
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
        }
        MultipartFile myFile = fileGen.getMyFile();
        BasAttachmentTypeEnum type = fileGen.getType();
        String uploadFolder = fileGen.getUploadFolder();
        String url = fileGen.getUrl();
        String mainUploadFolder = fileGen.getMainUploadFolder();
        if (null == myFile || StringUtils.isEmpty(uploadFolder) || StringUtils.isEmpty(url) || StringUtils.isEmpty(mainUploadFolder)) {
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPWAYBILL026, getLen());
        }
        Files res = new Files();
        // 获取保存的路径，本地磁盘中的一个文件夹
        String newName = "";
        String suffix = myFile.getOriginalFilename().substring(myFile.getOriginalFilename().indexOf(".") + 1);
        // 文件原名称
        if (fileGen.getRename()) {
            // 上传文件重命名
            newName = myFile.getOriginalFilename().substring(0, myFile.getOriginalFilename().indexOf(".")) + "_" + String.valueOf(System.currentTimeMillis()).concat(".").concat(suffix);
        } else {
            newName = myFile.getOriginalFilename().substring(0, myFile.getOriginalFilename().indexOf(".")).concat(".").concat(suffix);
        }
        type = Optional.ofNullable(type).orElse(BasAttachmentTypeEnum.PREFIX_TEMP);
        String prefix = lineToHump(type.getBusinessCode()) + "/" + type.getFileDirectory();
        try {
            // 这里使用Apache的FileUtils方法来进行保存
            //todo 按类型，时间戳 分类
            String yearMonth = DateUtils.dateTimeNow("yyyyMMdd");
            String lastFileStr = "/" + prefix + "/" + yearMonth + "/";
            uploadFolder = uploadFolder.trim().endsWith("/") ? uploadFolder.substring(0, uploadFolder.trim().length() - 1) : uploadFolder;
            File file = new File(uploadFolder + lastFileStr, newName);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            FileUtils.copyInputStreamToFile(myFile.getInputStream(), file);
            String path = lastFileStr + newName;
            log.info("文件地址path：{}", path);
            //  /home/jenkins/gfs/bas/20200927/78784353104545.png
            res.setPath(file.getAbsolutePath())
                    .setUrl((url.trim().endsWith("/") ? url.substring(0, url.trim().length() - 1) : url) + path)
                    .setFileName(newName)
                    .setSuffix(suffix)
                    .setSuccessFlag(1)
                    .setFileType(type.getBusinessCode())
                    .setFileTime(new Date());
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件失败", e);
        }
        return res;
    }


    /**
     * 重命名文件或文件夹
     *
     * @param resFilePath 源文件路径
     * @param newFileName 重命名
     * @return 操作成功标识
     */
    public static String renameFile(String resFilePath, String newFileName) {
        String newFilePath = StringToolkit.formatPath(StringToolkit.getParentPath(resFilePath) + File.separator + newFileName);
        File resFile = new File(resFilePath);
        File newFile = new File(newFilePath);
        boolean flag = resFile.renameTo(newFile);
        log.info("命名前{},重命名后{},命名结果{}", resFile.getPath(), newFile.getPath(), flag ? "成功" : "失败");
        if (flag) {
            return newFile.getPath();
        }
        return null;
    }

    public static String getFilePath(String fileUrl) {
        try {
            int index = fileUrl.lastIndexOf("/");
            String filePath = fileUrl.substring(0, index);
            return filePath;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileRelativePath(String fileUrl) {
        try {
            String filePath = getFilePath(fileUrl);
            filePath = filePath.split("//")[1];
            filePath = filePath.substring(filePath.indexOf("/"), filePath.length() - 1);
            return filePath;
        } catch (Exception e) {
            return "";
        }
    }

    public static String[] getFileNames(String fileUrl) {
        if (StringUtils.isEmpty(fileUrl)) {
            return null;
        }
        String[] temp = fileUrl.split("\\/");
        String[] fileName = temp[temp.length - 1].split("\\.");
        return fileName;
    }

    public static String getFileName(String fileUrl) {
        try {
            return getFileNames(fileUrl)[0];
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFileSuffix(String fileUrl) {
        try {
            return "." + getFileNames(fileUrl)[1];
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 下划线转驼峰
     */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                log.info("删除失败，附件不存在");
                return;
            }
            file.delete();
            log.info("附件删除成功：{}", filePath);
        } catch (Exception e) {
            log.info("附件删除异常：{}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println(getFilePath("http://183.3.221.229:22277/bil/bil/file/image/materials/20201208/1052311294568921269.jpg"));
        System.out.println(getFileName("http://183.3.221.229:22277/bil/bil/file/image/materials/20201208/1052311294568921269.jpg"));
        System.out.println(getFileSuffix("http://183.3.221.229:22277/bil/bil/file/image/materials/20201208/1052311294568921269.jpg"));
        System.out.println(getFileRelativePath("http://183.3.221.229:22277/bil/bil/file/image/materials/20201208/1052311294568921269.jpg"));
        deleteFile("E:\\home\\jenkins\\6pl\\file\\transferOrder\\orderFile\\20201210\\1607591927669 - 副本.jpg");
    }
}
