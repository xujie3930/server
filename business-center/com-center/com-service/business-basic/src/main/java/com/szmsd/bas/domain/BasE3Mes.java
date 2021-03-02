package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * E3消息表
 * </p>
 *
 * @author admin
 * @since 2020-11-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="E3消息表", description="BasE3Mes对象")
public class BasE3Mes extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(type = IdType.UUID)
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

    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "消息内容")
    @Excel(name = "消息内容")
    private String content;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号")
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "网点名称")
    private String siteName;

    @ApiModelProperty(value = "员工编号")
    @Excel(name = "员工编号")
    private String empCode;

    @ApiModelProperty(value = "员工姓名")
    @Excel(name = "员工姓名")
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

    @ApiModelProperty(value = "查看标识：0未读，1已读")
    @Excel(name = "查看标识：0未读，1已读")
    private String readFlag;

    @ApiModelProperty(value = "查看时间")
    @Excel(name = "查看时间")
    private Date readTime;

    @ApiModelProperty(value = "附件标识：0不含附件，1含附件")
    @Excel(name = "附件标识：0不含附件，1含附件")
    private String attachmentFlag;

    @ApiModelProperty(value = "附件url")
    @Excel(name = "附件url")
    private String attachmentUrl;

    @ApiModelProperty(value = "删除标识：0未删除，1已删除")
    @Excel(name = "删除标识：0未删除，1已删除")
    private String delFlag;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "最后修改人")
    @Excel(name = "最后修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;


}
