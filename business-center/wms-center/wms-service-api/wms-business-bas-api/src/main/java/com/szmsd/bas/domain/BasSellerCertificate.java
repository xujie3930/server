package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import java.util.Date;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 
    * </p>
*
* @author l
* @since 2021-03-10
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasSellerCertificate对象")
public class BasSellerCertificate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String country;

    @ApiModelProperty(value = "VAT账号")
    @Excel(name = "VAT账号")
    private String vat;

    @ApiModelProperty(value = "EORI账号")
    @Excel(name = "EORI账号")
    private String eori;

    @ApiModelProperty(value = "CNEE名称")
    @Excel(name = "CNEE名称")
    private String cneeName;

    @ApiModelProperty(value = "CNEE地址")
    @Excel(name = "CNEE地址")
    private String cneeAddress;

    @ApiModelProperty(value = "附件")
    @Excel(name = "附件")
    private String attachment;

    @ApiModelProperty(value = "登记时间")
    @Excel(name = "登记时间")
    private Date registrationDate;

    @ApiModelProperty(value = "是否生效")
    @Excel(name = "是否生效")
    private Boolean isActive;

    @ApiModelProperty(value = "证件是否有效")
    @Excel(name = "证件是否有效")
    private Boolean vaild;


}
