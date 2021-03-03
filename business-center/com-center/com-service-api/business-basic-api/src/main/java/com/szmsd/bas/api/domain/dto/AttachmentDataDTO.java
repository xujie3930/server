package com.szmsd.bas.api.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class AttachmentDataDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "附件下载url")
    @NotEmpty(message = "附件URL不能为空")
    private String attachmentUrl;

}
