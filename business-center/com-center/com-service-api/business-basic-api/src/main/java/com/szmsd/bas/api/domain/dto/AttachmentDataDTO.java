package com.szmsd.bas.api.domain.dto;

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
public class AttachmentDataDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "附件下载url")
    @NotEmpty(message = "附件URL不能为空")
    private String attachmentUrl;

    /**
     *  目前该字段仅用于一票多件
     */
    @ApiModelProperty(value = "箱标类型")
    private String attachmentType;

    @ApiModelProperty(value = "名称")
    private String attachmentName;

}
