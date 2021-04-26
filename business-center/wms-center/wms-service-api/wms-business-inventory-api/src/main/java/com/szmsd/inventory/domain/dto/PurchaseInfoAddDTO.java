package com.szmsd.inventory.domain.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;


/**
 * <p>
 * 采购单
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "Purchase对象")
public class PurchaseInfoAddDTO {
    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    List<PurchaseAddDTO> purchaseInfoAddDTOList;
    @ApiModelProperty(value = "出库单号-前端从列表携带")
    private List<String> orderNoList;
    @ApiModelProperty(value = "备注")
    private String remark;
}
