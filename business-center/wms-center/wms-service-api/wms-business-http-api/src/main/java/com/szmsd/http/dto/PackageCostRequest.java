package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PackageCostRequest", description = "PackageCostRequest对象")
public class PackageCostRequest {

    private String warehouseCode;

    private List<String> ProcessNoList;

    private String Currency;
}
