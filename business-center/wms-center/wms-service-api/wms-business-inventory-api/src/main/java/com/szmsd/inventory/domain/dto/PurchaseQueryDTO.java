package com.szmsd.inventory.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;


/**
 * <p>
 * 采购单
 * </p>
 *
 * @author 11
 * @since 2021-04-25
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "采购单", description = "Purchase对象")
public class PurchaseQueryDTO extends BaseEntity {

    @ApiModelProperty(value = "ID")
    private Integer id;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String customCode;

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
        Optional.ofNullable(customCode)
                .filter(StringUtil::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ","))
                .ifPresent(res -> customCodeList = Arrays.asList(res.split(",")));
    }

    private List<String> customCodeList;

    @ApiModelProperty(value = "采购单号")
    @Excel(name = "采购单号")
    private String purchaseNo;

    public void setPurchaseNo(String purchaseNo) {
        this.purchaseNo = purchaseNo;
        Optional.ofNullable(purchaseNo)
                .filter(StringUtil::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ","))
                .ifPresent(res -> purchaseNoList = Arrays.asList(res.split(",")));
    }

    private List<String> purchaseNoList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间 开始")
    private Date createTimeStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间 结束")
    private Date createTimeEnd;

}
