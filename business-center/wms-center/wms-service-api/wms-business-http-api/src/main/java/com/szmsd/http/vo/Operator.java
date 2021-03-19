package com.szmsd.http.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Operator {

    private String code;

    private String name;

    private String operatorType;

}
