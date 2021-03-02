package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * App消息表
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
@TableName("bas_app_mes")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasAppMes对象", description = "App消息表")
public class BasAppMes extends BaseEntity {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "主键ID")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "来源id")
    @Excel(name = "来源id")
    private String sourceId;

    @ApiModelProperty(value = "消息主类编码：1预警 2待办 3通知")
    @Excel(name = "消息主类编码：1预警 2待办 3通知")
    private String parentTypeCode;

    @ApiModelProperty(value = "消息主类")
    @Excel(name = "消息主类")
    private String parentTypeName;

    @ApiModelProperty(value = "消息子类编码（关联主子类别）")
    @Excel(name = "消息子类编码（关联主子类别）")
    private String subTypeCode;

    @ApiModelProperty(value = "消息子类")
    @Excel(name = "消息子类")
    private String subTypeName;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名")
    private String name;

    @ApiModelProperty(value = "手机号")
    @Excel(name = "手机号")
    private String phone;

    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private String address;

    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "消息内容")
    @Excel(name = "消息内容")
    private String content;

    @ApiModelProperty(value = "图片链接")
    @Excel(name = "图片链接")
    private String imgUrl;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "业务备注")
    @Excel(name = "业务备注")
    private String businessRemark;

    @ApiModelProperty(value = "员工id")
    @Excel(name = "员工id")
    private Long userId;

    @ApiModelProperty(value = "员工编号")
    @Excel(name = "员工编号")
    private String empCode;

    @ApiModelProperty(value = "员工名称")
    @Excel(name = "员工名称")
    private String empName;

    @ApiModelProperty(value = "数据时间")
    @Excel(name = "数据时间")
    private Date dateTime;

    @ApiModelProperty(value = "推送标识：0未推送，1已推送")
    @Excel(name = "推送标识：0未推送，1已推送")
    private String pushFlag;

    @ApiModelProperty(value = "推送时间")
    @Excel(name = "推送时间")
    private Date pushTime;

    @ApiModelProperty(value = "下次推送时间")
    @Excel(name = "下次推送时间")
    private Date nextPushTime;

    @ApiModelProperty(value = "重复推送次数")
    @Excel(name = "重复推送次数")
    private Integer repeatTimes;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;
}
