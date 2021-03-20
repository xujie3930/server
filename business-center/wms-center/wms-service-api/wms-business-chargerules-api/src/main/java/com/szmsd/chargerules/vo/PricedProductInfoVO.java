package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedProductInfoVO", description = "产品服务详情")
public class PricedProductInfoVO {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类型")
    private String type;

    @ApiModelProperty(value = "产品分类")
    private String category;

    @ApiModelProperty(value = "最小申报价值")
    private BigDecimal minDeclaredValue;

    @ApiModelProperty(value = "最大申报价值")
    private BigDecimal maxDeclaredValue;

    @ApiModelProperty(value = "挂号逾期天数")
    private Integer overdueDay;

    @ApiModelProperty(value = "挂号获取方式")
    private String trackingAcquireType;

    @ApiModelProperty(value = "時效最小值（天）")
    private Integer limitationDayMin;

    @ApiModelProperty(value = "時效最大值（天）")
    private Integer limitationDayMax;

    // 是否快递

    @ApiModelProperty(value = "支持发货类型")
    private List<String> shipmentTypeSupported;

    @ApiModelProperty(value = "系统可查询 - 是否可查询")
    private Boolean isShow;

    @ApiModelProperty(value = "客户可下单 - 是否可下单")
    private Boolean inService;

    // 有妥投轨迹

    @ApiModelProperty(value = "郡/省/州必填 - 包裹的州必须填写")
    private Boolean stateRequire;

    @ApiModelProperty(value = "城/镇必填 - 包裹的城市必须填写")
    private Boolean cityRequire;

    @ApiModelProperty(value = "郡/省/州验证 - 包裹的州验证")
    private Boolean stateValifition;

    @ApiModelProperty(value = "邮编必填 - 包裹的邮编必须填写")
    private Boolean postCodeRequire;

    @ApiModelProperty(value = "邮编验证 - 包裹的邮编验证")
    private Boolean postCodeValifition;

    @ApiModelProperty(value = "收件人必填 - 包裹的收件人必须填写")
    private Boolean recipientRequire;

    @ApiModelProperty(value = "Email必填 - 包裹的电子邮件Email必须填写")
    private Boolean emailRequire;

    @ApiModelProperty(value = "电话必填 - 包裹的电话号码必须填写")
    private Boolean phoneRequire;

    @ApiModelProperty(value = "电话验证 - 包裹的电话号码验证")
    private Boolean phoneValifition;

    @ApiModelProperty(value = "郡/省/州长度限制1 - 包裹的州最小长度限制")
    private Integer minStateLength;

    @ApiModelProperty(value = "郡/省/州长度限制2 - 包裹的州长度限制")
    private Integer stateLength;

    @ApiModelProperty(value = "城市长度限制1 - 包裹的最小城市长度限制")
    private Integer minCityLength;

    @ApiModelProperty(value = "城市长度限制2 - 包裹的城市长度限制")
    private Integer cityLength;

    @ApiModelProperty(value = "邮编长度限制1 - 邮编最小长度限制")
    private Integer minPostCodeLength;

    @ApiModelProperty(value = "邮编长度限制2 - 邮编长度限制")
    private Integer postCodeLength;

    @ApiModelProperty(value = "收件人长度限制1 - 包裹的最小收件人长度限制")
    private Integer minRecipientLength;

    @ApiModelProperty(value = "收件人长度限制2 - 包裹的收件人长度限制")
    private Integer recipientLength;

    @ApiModelProperty(value = "Email长度限制1 - 包裹的最小电子邮件Email长度限制")
    private Integer minEmailLength;

    @ApiModelProperty(value = "Email长度限制2 - 包裹的电子邮件Email长度限制")
    private Integer emailLength;

    @ApiModelProperty(value = "电话长度限制1 - 包裹的最小电话号码长度限制")
    private Integer minPhoneLength;

    @ApiModelProperty(value = "电话长度限制2 - 包裹的电话号码长度限制")
    private Integer phoneLength;

    @ApiModelProperty(value = "电话数字位数1 - 包裹的最小电话号码数字长度限制")
    private Integer minPhoneNumberLength;

    @ApiModelProperty(value = "电话数字位数2 - 包裹的最大电话号码数字长度限制")
    private Integer maxPhoneNumberLength;

    @ApiModelProperty(value = "地址1长度限制1 - 服务的最小地址1长度限制（Address1）")
    private Integer minAddressStreet1Length;

    @ApiModelProperty(value = "地址1长度限制2 - 服务的地址1长度限制（Address1）")
    private Integer addressStreet1Length;

    @ApiModelProperty(value = "地址2长度限制1 - 服务的最小地址2长度限制（Address2）")
    private Integer minAddressStreet2Length;

    @ApiModelProperty(value = "地址2长度限制2 - 服务的地址2长度限制（Address2）")
    private Integer addressStreet2Length;

    // 地址总长度限制

    @ApiModelProperty(value = "挂号服务名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "黑名单")
    private List<String> blackList;

    @ApiModelProperty(value = "白名单")
    private List<String> whiteList;

}
