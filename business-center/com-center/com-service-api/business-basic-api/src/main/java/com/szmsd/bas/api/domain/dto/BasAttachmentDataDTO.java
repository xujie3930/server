package com.szmsd.bas.api.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;


/**
 * <p>
 * 附件表 - 数据传输对象
 * </p>
 *
 * @author wangshuai
 * @since 2020-12-14
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BasAttachmentDataDTO", description = "BasAttachment附件表")
public class BasAttachmentDataDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "附件名")
    private String attachmentName;

    @ApiModelProperty(value = "附件类型")
    private String attachmentType;

    @ApiModelProperty(value = "附件下载url")
    @NotEmpty(message = "附件URL不能为空")
    private String attachmentUrl;

}
