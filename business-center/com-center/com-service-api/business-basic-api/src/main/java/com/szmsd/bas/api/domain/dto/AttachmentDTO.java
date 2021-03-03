package com.szmsd.bas.api.domain.dto;

import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import lombok.Builder;
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
@Builder
@Accessors(chain = true)
public class AttachmentDTO {

    /** 业务编号(单据号) **/
    private String businessNo;

    /** 业务明细号（非必传） **/
    private String businessItemNo;

    /** 附件路径 **/
    private List<AttachmentDataDTO> fileList;

    /** 附件业务枚举 **/
    private AttachmentTypeEnum attachmentTypeEnum;

}
