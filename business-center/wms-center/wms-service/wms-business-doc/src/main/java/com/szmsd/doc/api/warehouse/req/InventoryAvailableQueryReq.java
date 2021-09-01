package com.szmsd.doc.api.warehouse.req;

import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InventoryAvailableQueryReq extends QueryDto implements Serializable {
    @NotBlank(message = "目的仓库编码不能为空")
    @Size(max = 30,message = "目的仓库编码仅支持0-30字符")
    @ApiModelProperty(value = "目的仓库编码 [30]", required = true, example = "GZ")
    private String warehouseCode;
    @NotBlank(message = "客户编码不能为空")
    @Size(max = 30,message = "客户编码仅支持0-30字符")
    @ApiModelProperty(value = "客户编码 [30]", example = "CN72", required = true)
    private String cusCode;
    @Size(max = 30,message = "sku仅支持0-30字符")
    @ApiModelProperty(value = "sku - 模糊查询 [30]")
    private String sku;
    @Size(max = 30,message = "sku仅支持0-30字符")
    @ApiModelProperty(value = "sku - 精准查询 [30]", example = "SCN72000010")
    private String eqSku;

    @ApiModelProperty(value = "skus - 批量查询")
    private List<String> skus;

    @ApiModelProperty(value = "查询类型，1可用库存为0时不查询。2可用库存为0时查询。默认1")
    private Integer queryType = 1;
    @Size(max = 30)
    @ApiModelProperty(value = "只查询SKU，传值：SKU [30]")
    private String querySku;
    @Size(max = 30)
    @ApiModelProperty(value = "SKU来源，不传默认084002 [30]")
    private String source;

    public InventoryAvailableQueryDto convertThis() {
        InventoryAvailableQueryDto inventoryAvailableQueryDto = new InventoryAvailableQueryDto();
        BeanUtils.copyProperties(this, inventoryAvailableQueryDto);
        return inventoryAvailableQueryDto;
    }

}
