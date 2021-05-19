package com.szmsd.http.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class MaterialRequest {

    // 仓库编码 - 非业务数据
    @JSONField(serialize = false)
    private String warehouseCode;

    @ApiModelProperty(value = "初始重量g")
    @Excel(name = "初始重量g")
    private Double initWeight;

    @ApiModelProperty(value = "初始长 cm")
    @Excel(name = "初始长 cm")
    private Double initLength;

    @ApiModelProperty(value = "初始宽 cm")
    @Excel(name = "初始宽 cm")
    private Double initWidth;

    @ApiModelProperty(value = "初始高 cm")
    @Excel(name = "初始高 cm")
    private Double initHeight;
    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    @TableField("`code`")
    private String code;
    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "sku/包材")
    @Excel(name = "sku/包材")
    private String category;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;
}
