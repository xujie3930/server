package com.szmsd.http.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSkuInspectionRequest {

    /**
     * 仓库
     */
    private String warehouseCode;

    /**
     * 所属入库单号，可空。（不是验货单号，验货单号不用传）
     */
    private String refOrderNo;

    /**
     * 验货SKU列表
     */
    private List<String> details;

    public AddSkuInspectionRequest(String warehouseCode, List<String> details) {
        this.warehouseCode = warehouseCode;
        this.details = details;
    }

}
