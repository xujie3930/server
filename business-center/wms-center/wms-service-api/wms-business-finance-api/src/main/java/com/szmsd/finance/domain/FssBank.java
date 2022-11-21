package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "银行账号", description = "银行账号")
@TableName("fss_bank")
public class FssBank extends FssBaseEntity{

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "银行编码")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "账号")
    private String bankAccount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;
}
