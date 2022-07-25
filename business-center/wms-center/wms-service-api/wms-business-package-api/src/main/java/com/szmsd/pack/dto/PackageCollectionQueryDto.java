package com.szmsd.pack.dto;

import com.google.common.collect.Lists;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "PackageCollectionQueryDto", description = "PackageCollectionQueryDto对象")
public class PackageCollectionQueryDto extends QueryDto {

    @ApiModelProperty(value = "揽收单号")
    private String collectionNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "状态")
    private String status;

    @ApiModelProperty(value = "揽收人")
    private String collectionName;

    @ApiModelProperty(value = "揽收至仓库")
    private String collectionToWarehouse;

    @ApiModelProperty(value = "处理方式")
    private String handleMode;

    @ApiModelProperty(value = "创建时间")
    private String[] createTimes;

    @ApiModelProperty(value = "揽收时间")
    private String[] collectionTimes;

    @ApiModelProperty(value = "客户代码")
    private String customCode;


    @ApiModelProperty(value = "客户编码list")
    private List<String> customCodeList;

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
        this.customCodeList = StringUtils.isNotEmpty(customCode) ? Arrays.asList(customCode.split(",")) : Lists.newArrayList();
    }
}
