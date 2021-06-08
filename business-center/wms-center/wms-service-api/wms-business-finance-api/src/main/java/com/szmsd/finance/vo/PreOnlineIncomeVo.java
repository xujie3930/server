package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PreOnlineIncomeVo", description = "PreOnlineIncomeVo对象")
@AllArgsConstructor
@NoArgsConstructor
public class PreOnlineIncomeVo {

    private String serialNo;

    private String rechargeUrl;

}
