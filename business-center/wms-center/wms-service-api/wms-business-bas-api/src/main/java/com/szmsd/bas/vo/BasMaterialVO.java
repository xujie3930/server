package com.szmsd.bas.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasMaterialVO {
    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "包材名称")
    @Excel(name = "包材名称")
    private String productName;

    @ApiModelProperty(value = "包材编码")
    @Excel(name = "包材编码")
    @TableField("`code`")
    private String code;
}
