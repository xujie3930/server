package com.szmsd.bas.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * <p>
 * 附件表
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BasAttachmentQueryDto", description = "附件表查询")
public class BasAttachmentQueryDTO {

    @ApiModelProperty(value = "业务编码")
    private String businessCode;

    @ApiModelProperty(value = "业务编号")
    private String businessNo;

    @ApiModelProperty(value = "业务明细号")
    private String businessItemNo;

    @ApiModelProperty(value = "附件类型")
    private String attachmentType;

}
