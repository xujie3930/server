package com.szmsd.bas.dto;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value="BasChannelsDTO", description="渠道节点表")
public class BasChannelsDTO extends QueryDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家code")
    private String countryCode;

    @ApiModelProperty(value = "物流服务code")
    private String logisticsErvicesCode;

    @ApiModelProperty(value = "物流服务名")
    private String logisticsErvicesName;

    @ApiModelProperty(value = "下拉框选的仓库code集合(查询支持多选)")
    private List<String> warehouseCodeList;

//    @ApiModelProperty(value = "下拉框选的仓库名称集合")
//    private List<String> warehouseNameList;

    @ApiModelProperty(value = "批量删除的ID")
    private List<Integer> ids;
}
