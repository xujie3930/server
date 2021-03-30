package com.szmsd.exception.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.web.domain.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
* @since 2021-03-30
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="ExceptionInfo对象")
public class ExceptionInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private Integer version;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "操作员")
    @Excel(name = "操作员")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "关联单号")
    @Excel(name = "关联单号")
    private String orderNo;

    @ApiModelProperty(value = "异常号")
    @Excel(name = "异常号")
    private String exceptionNo;

    @ApiModelProperty(value = "单类型")
    @Excel(name = "单类型")
    private String orderType;

    @ApiModelProperty(value = "单类型名称")
    @Excel(name = "单类型名称")
    private String orderTypeName;

    @ApiModelProperty(value = "异常类型")
    @Excel(name = "异常类型")
    private String exceptionType;

    @ApiModelProperty(value = "异常类型名称")
    @Excel(name = "异常类型名称")
    private String exceptionTypeName;

    @ApiModelProperty(value = "是否系统自动创建")
    @Excel(name = "是否系统自动创建")
    private Boolean isAutoCreated;

    @ApiModelProperty(value = "处理备注")
    @Excel(name = "处理备注")
    private String processRemark;

    @ApiModelProperty(value = "处理方式")
    @Excel(name = "处理方式")
    private String processType;

    @ApiModelProperty(value = "处理方式名称")
    @Excel(name = "处理方式名称")
    private String processTypeName;

    @ApiModelProperty(value = "解决备注")
    @Excel(name = "解决备注")
    private String solveRemark;

    @ApiModelProperty(value = "是否解决")
    @Excel(name = "是否解决")
    private Boolean state;

    @ApiModelProperty(value = "是否处理")
    @Excel(name = "是否处理")
    private Boolean deal;

    @ApiModelProperty(value = "附件")
    @Excel(name = "附件")
    private String appendix;

    @ApiModelProperty(value = "处理人")
    @Excel(name = "处理人")
    private String dealName;

    @ApiModelProperty(value = "处理时间")
    @Excel(name = "处理时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dealDate;


}
