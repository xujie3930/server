package com.szmsd.putinstorage.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "入库单日志表结构" , description = "InboundReceiptRecord")
public class InboundReceiptRecord implements Serializable {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id" , type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "创建人名称")
    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建，提审，取消，审核，上架，完成")
    private String type;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库")
    @TableField(value = "warehouse_code", updateStrategy = FieldStrategy.NEVER, insertStrategy = FieldStrategy.NEVER)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseCodeName;

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "备注")
    private String remark;

}
