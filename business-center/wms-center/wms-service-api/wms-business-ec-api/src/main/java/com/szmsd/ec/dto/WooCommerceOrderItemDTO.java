package com.szmsd.ec.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * woocommerce订单信息明细表
 * </p>
 *
 * @author lyf
 * @since 2022-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WooCommerceOrderItemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "Woo Commerce 的唯一order ID")
    private String wooCommerceOrderId;

    // @ApiModelProperty(value = "订单记录id")
    // private Integer wooCommerceParentId;

    @ApiModelProperty(value = "商品明细id")
    private String itemId;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "商品id")
    private String productId;

    @ApiModelProperty(value = "变更id")
    private String variationId;

    @ApiModelProperty(value = "订购数量")
    private Integer quantity;

    @ApiModelProperty(value = "产品税级")
    private String taxClass;

    @ApiModelProperty(value = "小计(折扣前)")
    private String subtotal;

    @ApiModelProperty(value = "含税小计(折扣前)")
    private String subtotalTax;

    @ApiModelProperty(value = "小计(折扣后)")
    private String total;

    @ApiModelProperty(value = "含税小计(折扣后)")
    private String totalTax;

    @ApiModelProperty(value = "产品sku")
    private String sku;

    @ApiModelProperty(value = "产品价格")
    private String price;

    @ApiModelProperty(value = "税率信息JSON")
    private String taxes;

    @ApiModelProperty(value = "元数据JSON")
    private String metaData;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;


}
