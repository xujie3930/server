package com.szmsd.chargerules.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;


/**
 * <p>
 *
 * </p>
 *
 * @author 11
 * @since 2021-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "", description = "ChaOperation对象")
public class ChaOperation extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作类型名称")
    @Excel(name = "操作类型名称")
    private String operationTypeName;

    @ApiModelProperty(value = "仓库编号")
    @Excel(name = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "订单类型 出库单：Shipment 入库单：Receipt 退货单：Bounce")
    @Excel(name = "订单类型 出库单：Shipment 入库单：Receipt 退货单：Bounce")
    private String orderType;

    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCode;

    @ApiModelProperty(value = "客户名称 A,B")
    @Excel(name = "客户名称 A,B")
    private String cusNameList;

    @ApiModelProperty(value = "客户编码 CNI1,CNI2")
    @Excel(name = "客户编码 CNI1,CNI2")
    private String cusCodeList;

    @ApiModelProperty(value = "币别编码")
    @Excel(name = "币别编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    @Excel(name = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "生效时间")
    @Excel(name = "生效时间")
    private LocalDateTime effectiveTime;

    @ApiModelProperty(value = "失效时间")
    @Excel(name = "失效时间")
    private LocalDateTime expirationTime;


}
