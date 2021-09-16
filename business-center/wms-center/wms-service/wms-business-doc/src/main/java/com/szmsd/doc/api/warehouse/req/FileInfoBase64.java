package com.szmsd.doc.api.warehouse.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: FileInfoBase64
 * @Description:
 * @Author: 11
 * @Date: 2021-09-16 14:17
 */
@Data
@ApiModel(description = "文件信息-Base64")
public class FileInfoBase64 {
    @NotBlank(message = "文件后缀不能为空")
    @ApiModelProperty(value = "文件Base64", example = "xx")
    private String fileBase64;
    @NotBlank(message = "文件后缀不能为空")
    @ApiModelProperty(value = "文件后缀", example = "jpg")
    private String suffix;
    @ApiModelProperty(value = "文件名称", example = "xxx")
    private String fileName;
}
