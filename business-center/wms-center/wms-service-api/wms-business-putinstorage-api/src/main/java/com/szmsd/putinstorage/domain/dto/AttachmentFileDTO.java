package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class AttachmentFileDTO {

    @ApiModelProperty(value = "主键ID",example = "")
    private Integer id;

    @ApiModelProperty(value = "附件下载url",example = "http://183.3.221.136:22221/file/inboundReceipt/documents/20210915/Snipaste_2021-09-13_17-32-02_1631689838576.png")
    @NotEmpty(message = "附件URL不能为空")
    private String attachmentUrl;
    @ApiModelProperty(value = "文件名",example = "Snipaste_2021-09-13_17-32-02_1631689838576.png")
    @NotEmpty(message = "文件名")
    private String attachmentName;


    /**
     * 目前用于一票多件
     */
    @ApiModelProperty(value = "类型")
    private String attachmentType;

}
