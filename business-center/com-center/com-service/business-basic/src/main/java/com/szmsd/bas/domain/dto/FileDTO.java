package com.szmsd.bas.domain.dto;

import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

@Data
@Accessors(chain = true)
public class FileDTO {

    /**
     * 访问地址
     */
    private String url;

    /**
     * 文件对象
     */
    private MultipartFile myFile;

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
    private AttachmentTypeEnum type;

    /**
     * 是否重写文件名
     */
    private Boolean rename = true;
}
