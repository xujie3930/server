package com.szmsd.delivery.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReturnExpressFeeDto implements Serializable {

    private Integer id;

    private String fromOrderNo;

    private String fee;
}
