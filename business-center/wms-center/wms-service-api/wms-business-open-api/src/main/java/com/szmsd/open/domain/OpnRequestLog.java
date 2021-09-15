package com.szmsd.open.domain;

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
 * 请求日志
 * </p>
 *
 * @author asd
 * @since 2021-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "请求日志", description = "OpnRequestLog对象")
public class OpnRequestLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "请求路径")
    private String requestUri;

    @ApiModelProperty(value = "请求方法")
    private String requestMethod;

    @ApiModelProperty(value = "请求header")
    private String requestHeader;

    @ApiModelProperty(value = "请求body")
    private String requestBody;

    @ApiModelProperty(value = "请求时间")
    private Date requestTime;

    @ApiModelProperty(value = "响应header")
    private String responseHeader;

    @ApiModelProperty(value = "响应body")
    private String responseBody;

    @ApiModelProperty(value = "响应时间")
    private Date responseTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求时间-开始")
    @Excel(name = "请求时间")
    private Date requestTimeStart;

    @TableField(exist = false)
    @ApiModelProperty(value = "请求时间-结束")
    @Excel(name = "请求时间")
    private Date requestTimeEnd;
}
