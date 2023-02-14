package com.szmsd.track.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 定义轨迹状态
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Data
@TableName("track_config")
@ApiModel(value = "Config对象", description = "定义轨迹状态")
public class TrackConfig extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "TY轨迹状态（中文）")
    private String tyTrackStatusCn;

    @ApiModelProperty(value = "TY轨迹状态（英文）")
    private String tyTrackStatusEn;

    @ApiModelProperty(value = "OMS轨迹状态")
    private String omsTrackStatus;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Version
    private Long version;

    @ApiModelProperty(value = "删除状态 1已删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "渠道  1 开启")
    private Integer channelStatus;

    @ApiModelProperty(value = "客户  1 开启")
    private Integer customerStatus;

    @ApiModelProperty(value = "消费者 1 开启")
    private Integer consumerStatus;
}
