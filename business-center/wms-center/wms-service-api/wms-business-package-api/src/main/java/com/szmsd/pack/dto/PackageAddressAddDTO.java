package com.szmsd.pack.dto;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.pack.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * package - 交货管理 - 地址信息表
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "新增/修改地址", description = "新增/修改地址对象")
public class PackageAddressAddDTO implements BOConvert {

    //@NotEmpty(message = "用户code不能为空")
    @ApiModelProperty(value = "用户code", required = true)
    @Excel(name = "用户code")
    private String sellerCode;

    @ApiModelProperty(value = "主键ID")
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "是否未默认地址：0：非默认。1：默认")
    @Excel(name = "是否未默认地址：0：非默认。1：默认")
    private Integer defaultFlag;

    @ApiModelProperty(value = "联系人姓名")
    @Excel(name = "联系人姓名")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "国家 - 名称")
    @Excel(name = "国家 - 名称")
    private String countryNameZh;

    @ApiModelProperty(value = "国家 - 简码")
    @Excel(name = "国家 - 简码")
    private String countryCode;

    @ApiModelProperty(value = "国家 - 英文名")
    @Excel(name = "国家 - 英文名")
    private String countryNameEn;

    @ApiModelProperty(value = "省 - 名称")
    @Excel(name = "省 - 名称")
    private String provinceNameZh;

    @ApiModelProperty(value = "省 - 简码")
    @Excel(name = "省 - 简码")
    private String provinceCode;

    @ApiModelProperty(value = "省 - 英文名")
    @Excel(name = "省 - 英文名")
    private String provinceNameEn;

    @ApiModelProperty(value = "市 - 名称", required = true)
    @Excel(name = "市 - 名称")
    private String cityNameZh;

    @ApiModelProperty(value = "市 - 简码")
    @Excel(name = "市 - 简码")
    private String cityCode;

    @ApiModelProperty(value = "市 - 英文名")
    @Excel(name = "市 - 英文名")
    private String cityNameEn;

    @ApiModelProperty(value = "区 - 名称", required = true)
    @Excel(name = "区 - 名称")
    private String districtNameZh;

    @ApiModelProperty(value = "区 - 简码")
    @Excel(name = "区 - 简码")
    private String districtCode;

    @ApiModelProperty(value = "区 - 英文名")
    @Excel(name = "区 - 英文名")
    private String districtNameEn;

    @ApiModelProperty(value = "具体地址 - 中文名", required = true)
    @Excel(name = "具体地址 - 中文名")
    private String addressZh;

    @ApiModelProperty(value = "具体地址 - 英文名")
    @Excel(name = "具体地址 - 英文名")
    private String addressEn;

    @ApiModelProperty(value = "邮编")
    private String postCode;

}
