package com.szmsd.chargerules.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "BasProductService", description = "产品服务")
public class BasProductService extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "是否可查询")
    private Boolean isShow;

    @ApiModelProperty(value = "是否可下单")
    private Boolean inService;

    @ApiModelProperty(value = "挂号获取方式")
    private String trackingAcquireType;

    @ApiModelProperty(value = "服务渠道名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "服务商名称")
    private String terminalCarrier;

    @ApiModelProperty(value = "物流商Code")
    private String logisticsProviderCode;

    @ApiModelProperty(value = "装箱规则")
    private String packingRule;

    @ApiModelProperty(value = "发货规则")
    private String shipmentRule;

    @ApiModelProperty(value = "创建账号")
    private String createBy;

    @ApiModelProperty(value = "修改账号")
    private String updateBy;

    @ApiModelProperty(value = "有效标识")
    private String delFlag;

    @ApiModelProperty(value = "有效标识")
    private String compareTrackingno;

    @ApiModelProperty(value = "是否收货仓库1")
    private int recevieWarehouseStatus;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "客户名字")
    private String customName;





}