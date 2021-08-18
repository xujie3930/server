package com.szmsd.bas.domain.dto;

import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * <p>
 * 附件表 - 数据传输对象
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BasAttachmentDTO", description = "BasAttachmentDto对象")
public class BasAttachmentDTO {

    @ApiModelProperty(value = "业务编号(单据号)")
    private String businessNo;

    @ApiModelProperty(value = "业务明细号（非必传）")
    private String businessItemNo;

    @ApiModelProperty(value = "文件数据集合")
    private List<BasAttachmentDataDTO> fileList;

    @ApiModelProperty(value = "附件业务枚举")
    private AttachmentTypeEnum attachmentTypeEnum;

}
