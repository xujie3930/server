package com.szmsd.inventory.domain.dto;

import com.google.common.collect.Lists;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class InventoryCheckQueryDTO {

    @ApiModelProperty(value = "申请单号")
    private List<String> orderNoList;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "创建时间开始")
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    private String createTimeEnd;

    @ApiModelProperty(value = "客户编码list")
    private List<String> customCodeList;

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
        this.customCodeList = StringUtils.isNotEmpty(customCode) ? Arrays.asList(customCode.split(",")) : Lists.newArrayList();
    }
}
