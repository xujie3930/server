package com.szmsd.putinstorage.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "CK1-创建来货单(入库单)明细")
public class CustomerManifestItemsDTO {

    @NotBlank(message = "货物编号（库存编码或其他条码标识）不能为空")
    @ApiModelProperty(value = "货物编号（库存编码或其他条码标识）", notes = "", required = true)
    private String No;

    @ApiModelProperty(value = "上架库存编码（库存编码或其他条码标识）", notes = "")
    private String StorageNo;

    @NotNull(message = "客户填写的申报数量不能为空")
    @ApiModelProperty(value = "客户填写的申报数量", notes = "", required = true)
    private Integer CustomerQuantity;

    @NotBlank(message = "货物描述信息不能为空")
    @ApiModelProperty(value = "货物描述信息", notes = "", required = true)
    private String GoodsDescription;

    @NotBlank(message = "申报名称不能为空")
    @ApiModelProperty(value = "申报名称", notes = "", required = true)
    private String DeclareName;
}