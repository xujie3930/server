package com.szmsd.bas.api.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
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
public class BasAttachmentExcelDTO {



    @Excel(name = "页码")
    private String businessItem;


    @Excel(name = "识别的箱标号")
    private String businessNo;

    @Excel(name = "出库单号")
    private String remark;

}
