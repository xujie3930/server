package com.szmsd.track.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 轨迹配置
 * </p>
 *
 * @author wxf
 * @since 2023-02-14
 */
@Getter
@Setter
@TableName("track_config_rule")
@ApiModel(value = "ConfigRule对象", description = "轨迹配置")
public class TrackConfigRule extends BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发货规则")
    private String shipmentRule;

    @ApiModelProperty(value = "发货服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "TY轨迹状态（中文）")
    private String tyTrackStatusCn;

    @ApiModelProperty(value = "TY轨迹状态（英文）")
    private String tyTrackStatusEn;

    @ApiModelProperty(value = "OMS轨迹状态")
    private String omsTrackStatus;

    @ApiModelProperty(value = "物流商")
    private String prcTerminalCarrier;

    @ApiModelProperty(value = "关键词")
    private String keyword;

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

}
