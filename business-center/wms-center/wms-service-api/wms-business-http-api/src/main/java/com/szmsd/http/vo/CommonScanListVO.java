package com.szmsd.http.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.http.enums.RemoteConstant;
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
 *
 * @author huanggaosheng
 * @since 2021-11-10
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "CommonScanListVO对象")
public class CommonScanListVO {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @Excel(name = "ID")
    private Integer id;
    @ApiModelProperty(value = "扫描人")
    private String createByName;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "扫描时间")
    private LocalDateTime createTime;

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

    @ApiModelProperty(value = "异常信息")
    @Excel(name = "响应体")
    private String responseBody;
    @ApiModelProperty(value = "响应错误信息-请求参数+错误内容")
    @Excel(name = "响应错误信息")
    private String errorMsg;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "请求时间")
    @Excel(name = "请求时间")
    private LocalDateTime requestTime;

    @ApiModelProperty(value = "请求状态(0：待请求,1：执行失败，2：执行完成）")
    @Excel(name = "请求状态(0：待请求,1：执行失败，2：执行完成）")
    private Integer requestStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "实际请求时间")
    @Excel(name = "实际请求时间")
    private LocalDateTime reRequestTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    public void setScanType(Integer scanType) {
        this.scanType = scanType;
        this.scanTypeName = RemoteConstant.RemoteTypeEnum.getScanEnumByTypeOrElse(scanType).getTypeName();
    }

    @ApiModelProperty(value = "扫描类型")
    @Excel(name = "扫描类型")
    private String scanTypeName;

    @ApiModelProperty(value = "重试次数")
    @Excel(name = "重试次数")
    private Integer retryTimes;

}
