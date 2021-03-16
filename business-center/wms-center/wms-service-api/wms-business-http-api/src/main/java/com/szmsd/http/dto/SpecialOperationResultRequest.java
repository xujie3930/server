package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SpecialOperationResultRequest implements Serializable {

    private String operationOrderNo;

    private String status;

    private String remark;

}
