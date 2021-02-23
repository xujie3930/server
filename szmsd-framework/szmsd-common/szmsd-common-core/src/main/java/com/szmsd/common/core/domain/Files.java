package com.szmsd.common.core.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class Files {

    /**
     * 图片访问地址
     */
    private String url;
    /**
     * 文件路径
     */
    private String path;
    /**
     * 后缀
     */
    private String suffix;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件时间
     */
    private Date fileTime;
    /**
     * 成功上传标志 1 = 成功 ，默认 0
     */
    private int successFlag;

}
