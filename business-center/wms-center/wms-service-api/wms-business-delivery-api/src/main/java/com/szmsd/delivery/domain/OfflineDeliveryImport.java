package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName(value = "offline_delivery_import")
@ApiModel(value = "线下出库记录", description = "线下出库记录对象")
public class OfflineDeliveryImport extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    private String refNo;

    @ApiModelProperty(value = "平台标记单号")
    private String amazonLogisticsRouteId;

    @ApiModelProperty(value = "买家")
    private String sellerCode;

    @ApiModelProperty(value = "供应商")
    private String supplierName;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "发货服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "门牌号")
    private String houseNo;

    @ApiModelProperty(value = "城市或者省份")
    private String stateOrProvince;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "电话号码")
    private String phoneNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "")
    private String taxNumber;

    @ApiModelProperty(value = "cod")
    private String cod;

    @ApiModelProperty(value = "重量g")
    private BigDecimal weight;

    @ApiModelProperty(value = "发货时间")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "提审时间")
    private Date bringTime;

    @ApiModelProperty(value = "发货时间")
    private Date deliveryTime;

    @ApiModelProperty(value = "处理状态  INIT  订单  CREATE_ORDER 生成费用 CREATE_COST  推TY PUSH_TY  完成 COMPLETED  取消 CANCELED")
    private String dealStatus;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "长 CM")
    private BigDecimal length;

    @ApiModelProperty(value = "宽 CM")
    private BigDecimal width;

    @ApiModelProperty(value = "高 CM")
    private BigDecimal height;

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;




}
