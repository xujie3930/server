package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 业务操作日志
 * </p>
 *
 * @author asd
 * @since 2021-06-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "业务操作日志", description = "OperationLog对象")
public class OperationLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Long version;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单据号")
    @Excel(name = "单据号")
    private String invoiceNo;

    @ApiModelProperty(value = "单据类型")
    @Excel(name = "单据类型")
    private String invoiceType;

    @ApiModelProperty(value = "操作类型")
    @Excel(name = "操作类型")
    private String type;

    @ApiModelProperty(value = "操作内容")
    @Excel(name = "操作内容")
    private String content;

    @ApiModelProperty(value = "操作IP")
    @Excel(name = "操作IP")
    private String ip;


}
