package com.szmsd.inventory.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryWarning", description = "库存预警")
public class InventoryWarning implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createByName;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "批次")
    private String batchNo;

    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "客户")
    private String cusCode;

    @ApiModelProperty(value = "OMS库存数量")
    private Integer qty;

    @ApiModelProperty(value = "WMS实际库存数量")
    private Integer existQty;

    @ApiModelProperty(value = "CK1实际库存数量")
    private Integer ckQty;

    @TableField(exist = false)
    @ApiModelProperty(value = "CK1实际库存数量")
    private Integer differenceWithCk = 0;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "是否发送邮件0未发送，1已经发送")
    private String sendEmailFlag;

}
