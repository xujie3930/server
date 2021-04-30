package com.szmsd.bas.dto;

import lombok.Data;

import java.util.List;

@Data
public class BaseProductBatchQueryDto {

    private List<String> codes;

    private String sellerCode;
}
