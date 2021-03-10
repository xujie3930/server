package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 出库单
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单", description = "DelOutbound对象")
public class DelOutbound extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则")
    private String packingRule;

    @ApiModelProperty(value = "参照单号")
    private String refOrderNo;

    @ApiModelProperty(value = "是否必须按要求装箱")
    private Boolean isPackingByRequired;

    @ApiModelProperty(value = "是否优先发货")
    private Boolean isFirst;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    private String newSku;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;

    @ApiModelProperty(value = "操作类型，开始处理：Processing，已发货：Shipped，已取消：Canceled")
    private String operationType;

    @ApiModelProperty(value = "操作时间")
    private Date operationTime;

    @ApiModelProperty(value = "采购单号")
    private String purchaseNo;
}
