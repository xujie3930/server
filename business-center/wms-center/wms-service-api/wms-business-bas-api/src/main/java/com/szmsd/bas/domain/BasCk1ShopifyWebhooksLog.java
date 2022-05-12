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
 * ck1对应shopify的webhooks日志
 * </p>
 *
 * @author asd
 * @since 2022-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "ck1对应shopify的webhooks日志", description = "BasCk1ShopifyWebhooksLog对象")
public class BasCk1ShopifyWebhooksLog extends BaseEntity {

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

    @ApiModelProperty(value = "类型，customers/data_request，customers/redact，shop/redact")
    @Excel(name = "类型，customers/data_request，customers/redact，shop/redact")
    private String type;

    @ApiModelProperty(value = "请求内容")
    private String payload;

    @ApiModelProperty(value = "WebhookId")
    private String webhookId;

    @ApiModelProperty(value = "校验码")
    private String hmac;

    @ApiModelProperty(value = "店铺名称")
    private String shop;

    @ApiModelProperty(value = "API版本")
    private String apiVersion;

    @ApiModelProperty(value = "主题")
    private String topic;
}
