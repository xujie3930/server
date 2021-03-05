package com.szmsd.common.core.domain;

import com.szmsd.common.core.enums.FileTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author liyingfeng
 * @date 2020/9/27 12:52
 */
@Data
@Accessors(chain = true)
public class FileGen {

    /**
     * 访问地址
     */
    private String url;
    /**
     * 文件对象
     */
    private MultipartFile myfile;
    /**
     * 文件存储地址
     */
    private String uploadFolder;
    /**
     * 文件映射目录
     */
    private String mainUploadFolder;
    /**
     * 文件类型
     */
    private FileTypeEnum type;
    /**
     * 是否重写文件名
     */
    private Boolean rename = true;
}
