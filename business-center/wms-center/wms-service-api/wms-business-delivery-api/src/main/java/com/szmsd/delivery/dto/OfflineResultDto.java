package com.szmsd.delivery.dto;

import com.szmsd.delivery.domain.OfflineCostImport;
import com.szmsd.delivery.domain.OfflineDeliveryImport;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OfflineResultDto implements Serializable {

    private List<OfflineDeliveryImport> offlineDeliveryImports;

    private List<OfflineCostImport> offlineCostImportList;
}
