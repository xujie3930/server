package com.szmsd.doc.api.warehouse.resp;

import com.szmsd.bas.vo.BasWarehouseVO;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.ss.formula.functions.T;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseVO", description = "仓库列表")
public class BasWarehouseResp {

//    @ApiModelProperty(value = "主键ID")
//    private Long id;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称 - 当前系统语言")
    private String warehouseName;
//
//    @ApiModelProperty(value = "国家代码")
//    private String countryCode;

    @ApiModelProperty(value = "国家显示名称")
    private String countryDisplayName;

    @ApiModelProperty(value = "城市")
    private String city;
//
//    @ApiModelProperty(value = "时区")
//    private Integer timeZone;
//
//    @ApiModelProperty(value = "地址")
//    private String address;

//    @ApiModelProperty(value = "VAT")
//    private String isCheckVat;
//
//    @ApiModelProperty(value = "状态：0无效，1有效")
//    private String status;
//
//    @ApiModelProperty(value = "入库单是否人工审核：0自动审核，1人工审核")
//    private String inboundReceiptReview;

    public static BasWarehouseResp convertThis(Object source) {
        BasWarehouseResp availableListResp = null;
        if (source != null) {
            availableListResp = new BasWarehouseResp();
            BeanUtils.copyProperties(source, availableListResp);
        }
        return availableListResp;
    }
}
