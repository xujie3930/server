package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PricedProductCodesCriteria {

    private List<String> codes;

}
