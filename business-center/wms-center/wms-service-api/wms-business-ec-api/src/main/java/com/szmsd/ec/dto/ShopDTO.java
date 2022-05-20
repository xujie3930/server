package com.szmsd.ec.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author makejava
 * @version V1.0
 * @ClassName:Shop
 * @Description: 店铺资料DTO
 * @date 2020-09-15 10:06:16
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class ShopDTO {

    private static final long serialVersionUID = 589761327657832656L;

    private Long id;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;
    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerCode;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    /**
     * 所属平台编号
     */
    @ApiModelProperty(value = "所属平台编号")
    private String platformCode;
    /**
     * 所属平台名称
     */
    @ApiModelProperty(value = "所属平台名称")
    private String platformName;
    /**
     * 所属平台访问id
     */
    @ApiModelProperty(value = "所属平台访问id")
    private String platformAccessId;
    /**
     * 通行凭证
     */
    @ApiModelProperty(value = "通行凭证")
    private String voucher;
    /**
     * 密钥
     */
    @ApiModelProperty(value = "密钥")
    private String secretKey;
    /**
     * 卖家id
     */
    @ApiModelProperty(value = "卖家id")
    private String sellerId;
    /**
     * 启用标识（0不启用，1启用）
     */
    @ApiModelProperty(value = "启用标识（0不启用，1启用）")
    private Integer enabledState;

    @ApiModelProperty(value = "默认location_id")
    private String locationId;

}