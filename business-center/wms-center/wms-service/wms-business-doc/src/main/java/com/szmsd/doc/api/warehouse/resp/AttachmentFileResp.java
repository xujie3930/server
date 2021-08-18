package com.szmsd.doc.api.warehouse.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class AttachmentFileResp {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "附件-Base64")
    @NotEmpty(message = "附件URL不能为空")
    private String attachment;

    @NotEmpty(message = "文件名")
    private String attachmentName;

}
