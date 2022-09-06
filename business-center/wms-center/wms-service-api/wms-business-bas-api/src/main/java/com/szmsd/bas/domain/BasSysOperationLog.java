package com.szmsd.bas.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
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
* @author admin
* @since 2022-09-05
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasSysOperationLog对象")
public class BasSysOperationLog extends BaseEntity {

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
    private BigDecimal version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    @Excel(name = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "请求路径")
    @Excel(name = "请求路径")
    private String requestUri;

    @ApiModelProperty(value = "请求方法")
    @Excel(name = "请求方法")
    private String requestMethod;

    @ApiModelProperty(value = "请求header")
    @Excel(name = "请求header")
    private String requestHeader;

    @ApiModelProperty(value = "请求body")
    @Excel(name = "请求body")
    private String requestBody;

    @ApiModelProperty(value = "请求时间")
    @Excel(name = "请求时间")
    private Date requestTime;

    @ApiModelProperty(value = "响应header")
    @Excel(name = "响应header")
    private String responseHeader;

    @ApiModelProperty(value = "响应body")
    @Excel(name = "响应body")
    private String responseBody;

    @ApiModelProperty(value = "响应时间")
    @Excel(name = "响应时间")
    private Date responseTime;


}
