package com.szmsd.pack.dto;

import com.google.common.collect.Lists;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jodd.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * <p>
 * package - 交货管理 - 地址信息表
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "揽件列表查询条件", description = "揽件列表查询条件")
public class PackageMangQueryDTO{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键IDs")
    private List<Integer> ids;

    @ApiModelProperty(value = "联系人")
    private String linkUserName;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除", hidden = true)
    private Integer delFlag = 0;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        Optional.ofNullable(orderNo)
                .filter(StringUtil::isNotBlank)
                .map(x -> x.replace(" ", ","))
                .map(x -> x.replace("，", ","))
                .map(x -> x.replace(";", ","))
                .ifPresent(res -> orderNoList = Arrays.asList(res.split(",")));
    }

    @ApiModelProperty(value = "订单号", hidden = true)
    private List<String> orderNoList;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @DateTimeFormat( pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间-开始")
    private LocalDate submitTimeStart;

    @DateTimeFormat( pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间-结束")
    private LocalDate submitTimeEnd;

    @DateTimeFormat( pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "截至揽收时间-开始")
    private LocalDate expectedDeliveryTimeStart;

    @DateTimeFormat( pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "截至揽收时间-结束")
    private LocalDate expectedDeliveryTimeEnd;

    @ApiModelProperty(value = "是否导出【 0：未导出，1：已导出】", example = "0")
    private Integer exportType;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "客户编码list")
    private List<String> customCodeList;

    public void setCustomCode(String customCode) {
        this.customCode = customCode;
        this.customCodeList = StringUtils.isNotEmpty(customCode) ? Arrays.asList(customCode.split(",")) : Lists.newArrayList();
    }
}
