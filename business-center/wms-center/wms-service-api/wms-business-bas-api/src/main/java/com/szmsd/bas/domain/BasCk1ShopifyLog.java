package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * ck1对应shopify请求日志
 * </p>
 *
 * @author asd
 * @since 2022-05-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ck1对应shopify请求日志", description = "BasCk1ShopifyLog对象")
public class BasCk1ShopifyLog extends BaseEntity {

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

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "类型，1shopify跳转ck1，2ck1跳转shopify，3登录")
    @Excel(name = "类型，1shopify跳转ck1，2ck1跳转shopify，3登录")
    private String type;

    @ApiModelProperty(value = "授权码")
    @Excel(name = "授权码")
    private String code;

    @ApiModelProperty(value = "校验码")
    @Excel(name = "校验码")
    private String hmac;

    @ApiModelProperty(value = "临时码")
    @Excel(name = "临时码")
    private String state;

    @ApiModelProperty(value = "host")
    @Excel(name = "host")
    private String host;

    @ApiModelProperty(value = "店铺名称")
    @Excel(name = "店铺名称")
    private String shop;

    @ApiModelProperty(value = "时间")
    @Excel(name = "时间")
    private String timestamp;

    @ApiModelProperty(value = "其它参数")
    @Excel(name = "其它参数")
    private String otherParams;

    @ApiModelProperty(value = "api key")
    @Excel(name = "api key")
    private String clientId;

    @ApiModelProperty(value = "访问权限，逗号分割")
    @Excel(name = "访问权限，逗号分割")
    private String scope;

    @ApiModelProperty(value = "重定向地址")
    @Excel(name = "重定向地址")
    private String redirectUri;

    @ApiModelProperty(value = "验证结果")
    @Excel(name = "验证结果")
    private String verifyState;
}
