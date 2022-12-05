package com.szmsd.delivery.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OfflineReadDto implements Serializable {

    private List<OfflineCostExcelDto> offlineCostExcelDtoList;

    private List<OfflineDeliveryExcelDto> offlineDeliveryExcelList;
}
