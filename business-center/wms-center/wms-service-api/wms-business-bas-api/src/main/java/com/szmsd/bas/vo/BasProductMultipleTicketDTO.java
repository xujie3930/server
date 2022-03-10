package com.szmsd.bas.vo;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: BasProductMultipleTicketDTO
 * @Description: 一票多见导入对象
 * @Author: 11
 * @Date: 2022-03-10 14:35
 */
@Data
public class BasProductMultipleTicketDTO {

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "是否一票多件")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "是否一票多件")
    @Excel(name = "是否一票多件")
    private String multipleTicketFlagStr;

    public void setMultipleTicketFlagStr(String multipleTicketFlagStr) {
        this.multipleTicketFlagStr = multipleTicketFlagStr;
        if (StringUtils.isNotBlank(multipleTicketFlagStr) && "是".equals(multipleTicketFlagStr)) {
            this.multipleTicketFlag = 1;
        } else {
            this.multipleTicketFlag = 0;
        }
    }

    @ApiModelProperty(value = "是否一票多件")
    private Integer multipleTicketFlag;
}
