package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * <p>
 * 公告通知明细
 * </p>
 *
 * @author ziling
 * @since 2020-10-14
 */
@TableName("bas_notice")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasNotice对象", description = "公告通知明细")
public class BasNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "标题")
    @Excel(name = "标题")
    private String title;

    @ApiModelProperty(value = "公告内容")
    @Excel(name = "公告内容")
    private String content;

    @ApiModelProperty(value = "接收网点编号")
    @Excel(name = "接收网点编号")
    private String receiveSiteCode;

    @ApiModelProperty(value = "接收网点名称")
    @Excel(name = "接收网点名称")
    private String receiveSiteName;

    @ApiModelProperty(value = "接收人编号")
    @Excel(name = "接收人编号")
    private String receiverCode;

    @ApiModelProperty(value = "接收人名称")
    @Excel(name = "接收人名称")
    private String receiverName;

    @ApiModelProperty(value = "接收端")
    @Excel(name = "接收端")
    private String receivePlatform;

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

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "附件名称")
    @Excel(name = "附件名称")
    private String attachmentName;

    @ApiModelProperty(value = "附件url")
    @Excel(name = "附件url")
    private String attachmentUrl;

    @ApiModelProperty(value = "创建网点编号")
    @Excel(name = "创建网点编号")
    private String createSiteCode;

    @ApiModelProperty(value = "创建网点名称")
    @Excel(name = "创建网点名称")
    private String createSiteName;


    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间")
    private String strDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间")
    private String endDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "接收人list")
    private List<Emp> receiveList;

}
