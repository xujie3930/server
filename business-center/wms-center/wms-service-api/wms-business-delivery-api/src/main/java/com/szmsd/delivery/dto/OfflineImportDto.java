package com.szmsd.delivery.dto;

import lombok.Data;

@Data
public class OfflineImportDto {

    private Long id;

    private String orderNo;

    private String trackingNo;

    private String dealStatus;

    private String errorMsg;

}
