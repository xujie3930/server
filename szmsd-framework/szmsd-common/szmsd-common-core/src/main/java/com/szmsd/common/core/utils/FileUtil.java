package com.szmsd.common.core.utils;

import com.szmsd.common.core.constant.CommonConstant;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.FilesDto;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.enums.FileTypeEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static com.szmsd.common.core.web.controller.BaseController.getLen;


/**
 * 文件上传工具类
 *
 * @author zhaopeng
 * @date 2020-07-16
 * @description
 */
@Slf4j
public class FileUtil {

    /**
     *
     * @param url 访问图片请求路径， 各自服务dev配置文件 url
     * @param myfile
     * @param uploadFolder 文件存放文件夹， 各自服务dev配置文件 uploadFolder
     * @return
     */
    @Deprecated
    public static Files getFileUrl(String url, MultipartFile myfile, String uploadFolder) {
        return getFileUrl(new FilesDto().setUrl(url).setMyfile(myfile).setUploadFolder(uploadFolder));
    }

    /**
     * 如果需要记录表中，feign 调用bil模块，BilFile接口
     * @param filesDto
     * @return
     */
    public static Files getFileUrl(FilesDto filesDto) {
        if(null == filesDto){
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
        }
        MultipartFile myfile = filesDto.getMyfile();
        FileTypeEnum type = filesDto.getType();
        String uploadFolder = filesDto.getUploadFolder();
        String url = filesDto.getUrl();
        String mainUploadFolder = filesDto.getMainUploadFolder();
        if(null == myfile || StringUtils.isEmpty(uploadFolder) || StringUtils.isEmpty(url) || StringUtils.isEmpty(mainUploadFolder)){
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPWAYBILL026, getLen());
        }
        // 获取保存的路径，本地磁盘中的一个文件夹
        Files res = new Files();
        Long time = System.nanoTime();
        String suffix = myfile.getOriginalFilename().substring(myfile.getOriginalFilename().indexOf(".") + 1);
        String fileName = "";
        if(filesDto.getRename()){
            // 上传文件重命名
            fileName = (time + (Math.round((Math.random()+1) * 1000)+ ""));
        }else{
            //原名称
            fileName = myfile.getOriginalFilename().substring(0,myfile.getOriginalFilename().indexOf("."));
        }
        String originalFilename = fileName.concat(".").concat(suffix);

        String prefix = "";
        type = null == type?FileTypeEnum.OTHER:type;
         switch (type){
            case WAYBILL:prefix = CommonConstant.PREFIX_FILE_WAYBILL;break;
            case TEMP: prefix = CommonConstant.PREFIX_TEMP;break;
            case PROBLEM:prefix = CommonConstant.PREFIX_PROBLEM_IMAGE;break;
            case USER:prefix = CommonConstant.PREFIX_FILE_USER;break;
            case SIGN:prefix = CommonConstant.PREFIX_FILE_SIGN;break;
            case NOTICE:prefix = CommonConstant.PREFIX_FILE_NOTICE;break;
            default:prefix = CommonConstant.PREFIX_FILE;break;
        }
        try {
            // 这里使用Apache的FileUtils方法来进行保存
            String yearMonth = DateUtils.dateTimeNow("yyyyMMdd");
            String lastFileStr = prefix + "/" + yearMonth + "/";
            uploadFolder = uploadFolder.trim().endsWith("/")?uploadFolder.substring(0,uploadFolder.trim().length()-1):uploadFolder;
            File file = new File(uploadFolder + lastFileStr, originalFilename);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            FileUtils.copyInputStreamToFile(myfile.getInputStream(), file);
            String path = lastFileStr + originalFilename;
            log.info("文件地址path：{}",file.toString());

            String subStrUrl = uploadFolder.substring(uploadFolder.indexOf(mainUploadFolder) + mainUploadFolder.length());
            res.setPath(file.getAbsolutePath())
                    .setUrl((url.trim().endsWith("/")?url.substring(0,url.trim().length()-1):url) + subStrUrl+ path)
                    .setSuffix(suffix)
                    .setFileName(fileName)
                    .setSuccessFlag(1)
                    .setFileType(type.getCode())
                    .setFileTime(new Date())
            ;

        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
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
        String newFilePath = StringToolkit.formatPath(StringToolkit.getParentPath(resFilePath) + "/" + newFileName);
        File resFile = new File(resFilePath);
        File newFile = new File(newFilePath);
        boolean flag = resFile.renameTo(newFile);
        if (flag) {
            return  newFile.getPath();
        }
        return "";
    }
}
