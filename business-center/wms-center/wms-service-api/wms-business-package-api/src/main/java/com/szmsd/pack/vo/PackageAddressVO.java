package com.szmsd.pack.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


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
@ApiModel(value = "package - 交货管理 - 地址信息表", description = "PackageAddress对象")
public class PackageAddressVO {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "是否未默认地址：0：非默认。1：默认")
    private Integer defaultFlag;

    public void setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
        switch (defaultFlag) {
            case 0:
                defaultFlagStr = "否";
                break;
            case 1:
                defaultFlagStr = "是";
                break;
            default:
                defaultFlagStr = "否";
        }
    }

    @ApiModelProperty(value = "是否未默认地址：0：非默认。1：默认")
    @Excel(name = "是否是默认地址")
    private String defaultFlagStr;


    @ApiModelProperty(value = "联系人姓名")
    @Excel(name = "联系人姓名")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "省 - 名称")
    @Excel(name = "省 - 名称")
    private String provinceNameZh;

    @ApiModelProperty(value = "省 - 简码")
    private String provinceCode;

    @ApiModelProperty(value = "省 - 英文名")
    private String provinceNameEn;

    @ApiModelProperty(value = "市 - 名称")
    @Excel(name = "市 - 名称")
    private String cityNameZh;

    @ApiModelProperty(value = "市 - 简码")
    private String cityCode;

    @ApiModelProperty(value = "市 - 英文名")
    private String cityNameEn;

    @ApiModelProperty(value = "区 - 名称")
    @Excel(name = "区 - 名称")
    private String districtNameZh;

    @ApiModelProperty(value = "区 - 简码")
    private String districtCode;

    @ApiModelProperty(value = "区 - 英文名")
    private String districtNameEn;

    @ApiModelProperty(value = "具体地址 - 中文名")
    private String addressZh;

    @ApiModelProperty(value = "具体地址 - 英文名")
    private String addressEn;

    @ApiModelProperty(value = "列表展示-详细地址", notes = "详细地址由新建时的：省+市+区+具体地址 拼成")
    @Excel(name = "详细地址")
    private String showAddr;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    public void setShowAddr() {
        this.showAddr = String.join(" ", this.getProvinceNameZh(), this.getCityNameZh(), this.getDistrictNameZh(), this.getAddressZh());
    }
}
