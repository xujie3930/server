package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单标签重试记录", description = "DelOutboundRetryLabel对象")
public class DelCk1RequestLog extends BaseEntity {

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

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "请求体")
    private String requestBody;

    @ApiModelProperty(value = "响应体")
    private String responseBody;

    @ApiModelProperty(value = "状态，WAIT，FAIL_CONTINUE，FAIL，SUCCESS")
    private String state;

    @ApiModelProperty(value = "失败次数")
    private Integer failCount;

    @ApiModelProperty(value = "上一次请求耗时时间")
    private Integer lastRequestConsumeTime;

    @ApiModelProperty(value = "下次重试时间")
    private Date nextRetryTime;

    @ApiModelProperty(value = "接口类型")
    private String type;

    @ApiModelProperty(value = "接口地址")
    private String url;

    @TableField(exist = false)
    @ApiModelProperty(value = "创建时间")
    private String[] createTimes;

    @TableField(exist = false)
    @ApiModelProperty(value = "IDS")
    private Long[] ids;
}
