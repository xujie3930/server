package com.szmsd.putinstorage.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName: InventoryStockByRangeDTO
 * @Description:
 * @Author: 11
 * @Date: 2021-10-22 11:14
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "sku入库状况查询条件")
public class InventoryStockByRangeDTO {

    @ApiModelProperty(value = "SKU",example = "[\"202110110001\"]")
    private List<String> skuList;

    public void setSkuList(List<String> skuList) {
        this.skuList = skuList;
        if (CollectionUtils.isNotEmpty(skuList)){
            this.skuList = skuList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        }
    }

    @NotNull(message = "开始时间不能为空或格式不正确")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss",example = "2021-10-01 10:10:10", required = true)
    private Date timeStart;

    @NotNull(message = "结束时间不能为空或格式不正确")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss",example = "2021-10-21 10:10:10", required = true)
    private Date timeEnd;

    public void valid() {
        if (timeEnd.before(timeStart)) throw new RuntimeException("结束时间不能在开始时间前");
    }

}
