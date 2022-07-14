package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 子母单-子表信息
 *
 * @author: taoJie
 * @since: 2022-07-13
 */

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "子母单-子表信息", description = "BasChildParentChild对象")
public class BasChildParentChild extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "母单客户代码")
    private String parentSellerCode;

    @ApiModelProperty(value = "申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyTime;

    @ApiModelProperty(value = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealTime;

    @ApiModelProperty(value = "附件URL")
    private String attachment;

    @ApiModelProperty(value = "状态 1审核中 2驳回 3已绑定 4解绑")
    private String state;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "删除标识：0未删除 2已删除")
    private String delFlag;


    @ApiModelProperty(value = "姓")
    @TableField(exist = false)
    private String nameCn;

    @ApiModelProperty(value = "名")
    @TableField(exist = false)
    private String nameEn;

    @ApiModelProperty(value = "公司")
    @TableField(exist = false)
    private String company;

    @ApiModelProperty(value = "认证状态 ")
    @TableField(exist = false)
    private Boolean certStatus;

    @ApiModelProperty(value = "联系电话")
    @TableField(exist = false)
    private String phoneNumber;

    @ApiModelProperty(value = "关联客户编码")
    @TableField(exist = false)
    private String childCodes;


}