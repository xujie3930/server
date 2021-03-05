package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 附件表
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "BasAttachment", description = "BasAttachment附件表")
@JsonIgnoreProperties(value={"params", "dataScope", "updateBy", "updateByName", "updateTime", "beginTime", "endTime"})
public class BasAttachment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "业务编码")
    @Excel(name = "业务编码")
    private String businessCode;

    @ApiModelProperty(value = "业务类型")
    @Excel(name = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "业务编号")
    @Excel(name = "业务编号")
    private String businessNo;

    @ApiModelProperty(value = "业务明细号")
    @Excel(name = "业务明细号")
    private String businessItemNo;

    @ApiModelProperty(value = "附件类型")
    @Excel(name = "附件类型")
    private String attachmentType;

    @ApiModelProperty(value = "附件ID")
    @Excel(name = "附件ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer attachmentId;

    @ApiModelProperty(value = "附件名称")
    @Excel(name = "附件名称")
    private String attachmentName;

    @ApiModelProperty(value = "附件存储路径")
    @Excel(name = "附件存储路径")
    private String attachmentPath;

    @ApiModelProperty(value = "附件大小")
    @Excel(name = "附件大小")
    private Double attachmentSize;

    @ApiModelProperty(value = "附件下载URL")
    @Excel(name = "附件下载URL")
    private String attachmentUrl;

    @ApiModelProperty(value = "附件格式")
    @Excel(name = "附件格式")
    private String attachmentFormat;

    @ApiModelProperty(value = "创建ID",hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID",hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

}
