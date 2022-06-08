package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 查件服务反馈
    * </p>
*
* @author Administrator
* @since 2022-06-08
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="查件服务反馈", description="DelQueryServiceFeedback对象")
public class DelQueryServiceFeedback extends BaseEntity {

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

    @ApiModelProperty(value = "主数据id")
    @Excel(name = "主数据id")
    private Integer mainId;

    @ApiModelProperty(value = "操作类型")
    @Excel(name = "操作类型")
    private String type;

    @ApiModelProperty(value = "查件原因")
    @Excel(name = "查件原因")
    private String reason;


}
