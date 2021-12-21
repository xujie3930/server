package com.szmsd.http.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * <p>
 * 扫描执行任务
 * </p>
 * 每天抓取三天的数据
 * @author huanggaosheng
 * @since 2021-11-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(description = "扫描执行任务已完成")
public class CommonScanFinish extends BaseEntity {

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

    @ApiModelProperty(value = "请求头")
    @Excel(name = "请求头")
    private String requestHead;
    @ApiModelProperty(value = "请求token")
    @Excel(name = "请求token")
    private String requestToken;
    @ApiModelProperty(value = "请求参数")
    @Excel(name = "请求参数")
    private String requestParams;

    @ApiModelProperty(value = "请求url")
    @Excel(name = "请求url")
    private String requestUri;

    @ApiModelProperty(value = "响应头")
    @Excel(name = "响应头")
    private String responseHeader;

    @ApiModelProperty(value = "响应体")
    @Excel(name = "响应体")
    private String responseBody;

    @ApiModelProperty(value = "响应错误信息-请求参数+错误内容")
    @Excel(name = "响应错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "请求时间")
    @Excel(name = "请求时间")
    private LocalDateTime requestTime;

    @ApiModelProperty(value = "请求状态(0：待请求,1：执行失败，2：执行完成）")
    @Excel(name = "请求状态(0：待请求,1：执行失败，2：执行完成）")
    private Integer requestStatus;

    @ApiModelProperty(value = "实际请求时间")
    @Excel(name = "实际请求时间")
    private LocalDateTime reRequestTime;

    @ApiModelProperty(value = "实际请求时间")
    @Excel(name = "实际请求时间")
    private LocalDateTime reResponseTime;

    @ApiModelProperty(value = "巴枪编码")
    @Excel(name = "巴枪编码")
    private String gunCode;

    @ApiModelProperty(value = "appid")
    @Excel(name = "appid")
    private String appId;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "扫描类型")
    @Excel(name = "扫描类型")
    private Integer scanType;

    @ApiModelProperty(value = "重试次数")
    @Excel(name = "重试次数")
    private Integer retryTimes;


}
