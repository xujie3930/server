package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class AttachmentFileDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "附件下载url")
    @NotEmpty(message = "附件URL不能为空")
    private String attachmentUrl;

}
