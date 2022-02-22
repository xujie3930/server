package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * bas_warehouse - 仓库
 * </p>
 *
 * @author liangchao
 * @since 2021-03-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "bas_warehouse - 仓库", description = "BasWarehouse对象")
public class BasWarehouse extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库英文名")
    private String warehouseNameEn;

    @ApiModelProperty(value = "仓库中文名")
    private String warehouseNameCn;

    @ApiModelProperty(value = "是否需要VAT：0不需要，1需要")
    private String isCheckVat;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家中文名")
    private String countryChineseName;

    @ApiModelProperty(value = "国家显示名称")
    private String countryDisplayName;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "邮编")
    private String postcode;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "时区")
    private Integer timeZone;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

    @ApiModelProperty(value = "入库单是否人工审核：0自动审核，1人工审核")
    private String inboundReceiptReview;

    @ApiModelProperty(value = "创建ID",hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID",hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

}
