package com.szmsd.track.vo;

import com.szmsd.common.core.web.page.PageDomain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TrackConfigRuleQueryVO extends PageDomain {

    @ApiModelProperty(value = "OMS轨迹状态")
    private String omsTrackStatus;

    @ApiModelProperty(value = "TY轨迹状态（中文）")
    private String tyTrackStatusCn;

    @ApiModelProperty(value = "物流商")
    private String prcTerminalCarrier;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

}
