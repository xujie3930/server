package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import javax.validation.constraints.NotBlank;


/**
* <p>
    * sku规则匹配表
    * </p>
*
* @author Administrator
* @since 2022-05-10
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="sku规则匹配表", description="BasSkuRuleMatching对象")
public class BasSkuRuleMatching extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
            @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "创建人编号")
    @Excel(name = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    @Excel(name = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "源系统sku")
    @Excel(name = "源系统sku")
    @NotBlank(message = "源系统sku不能为空")
    private String sourceSku;

    @ApiModelProperty(value = "OMS SKU")
    @Excel(name = "OMS SKU")
    @NotBlank(message = "omsSku不能为空")
    private String omsSku;

    @ApiModelProperty(value = "系统类型0.Shopify")
    @Excel(name = "系统类型0.Shopify")
    private String systemType;



    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;


}
