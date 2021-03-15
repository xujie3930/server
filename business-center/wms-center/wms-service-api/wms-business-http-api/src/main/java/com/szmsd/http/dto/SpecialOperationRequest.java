package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SpecialOperationRequest implements Serializable {

    private String operationType;

    private String operationTypeDesc;

    private String unit;

    private String remark;

}
