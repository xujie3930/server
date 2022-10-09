package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.bas.domain.BasSellerMessage;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasSellerMessageQueryDTO extends BasSellerMessage {
    @ApiModelProperty(value = "创建时间-查询使用")
    @Excel(name = "创建时间-查询使用")
    private  String[] createTimes;


    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String type;

    @ApiModelProperty(value = "类型")
    @TableField(exist = false)
    private String sellerCodets;

    @ApiModelProperty(value = "类型集合")
    @TableField(exist = false)
    private List<String> sellerCodes;

    @ApiModelProperty(value = "客户端传用户名")
    @TableField(exist = false)
    private String userName;

}
