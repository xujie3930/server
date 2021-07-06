package com.szmsd.http.dto;

import com.szmsd.http.vo.WeightAddition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "UpdatePricedProductCommand", description = "修改产品的命令")
public class UpdatePricedProductCommand {

    @ApiModelProperty(value = "需要更改的产品的Code")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类型")
    private String type;

    @ApiModelProperty(value = "产品分类")
    private String category;

    @ApiModelProperty(value = "产品服务")
    private String service;

    @ApiModelProperty(value = "支持发货类型")
    private List<String> shipmentTypeSupported;

    @ApiModelProperty(value = "挂号逾期天数")
    private Integer overdueDay;

    @ApiModelProperty(value = "挂号服务名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "终端运输商")
    private String terminalCarrier;

    @ApiModelProperty(value = "轨迹官网地址")
    private String trackWebsite;

    @ApiModelProperty(value = "子产品")
    private List<String> subProducts;

    @ApiModelProperty(value = "是否挂号服务")
    private Boolean isTracking;

    @ApiModelProperty(value = "必须导入挂号")
    private Boolean forceImportTracking;

    @ApiModelProperty(value = "是否可查询")
    private Boolean isShow;

    @ApiModelProperty(value = "是否可下单")
    private Boolean inService;

    @ApiModelProperty(value = "是否支持一票多件发货")
    private Boolean isMultiPackageShipment;

    @ApiModelProperty(value = "客户能否导入挂号")
    private Boolean canImportTracking;

    @ApiModelProperty(value = "打印地址标签（是否必须存在挂号）")
    private Boolean forcePrintLableWithTracking;

    @ApiModelProperty(value = "挂号获取方式")
    private String trackingAcquireType;

    @ApiModelProperty(value = "包裹的州必须填写")
    private Boolean stateRequire;

    @ApiModelProperty(value = "包裹的州长度限制")
    private Integer stateLength;

    @ApiModelProperty(value = "包裹的州验证")
    private Boolean stateValifition;

    @ApiModelProperty(value = "包裹的城市必须填写")
    private Boolean cityRequire;

    @ApiModelProperty(value = "包裹的城市长度限制")
    private Integer cityLength;

    @ApiModelProperty(value = "服务的地址1长度限制（Address1）")
    private Integer addressStreet1Length;

    @ApiModelProperty(value = "服务的地址2长度限制（Address2）")
    private Integer addressStreet2Length;

    @ApiModelProperty(value = "服务的地址3长度限制（Address3）")
    private Integer addressStreet3Length;

    @ApiModelProperty(value = "服务的地址长度限制(Address1+Address2)")
    private Integer addressLength;

    @ApiModelProperty(value = "包裹的邮编必须填写")
    private Boolean postCodeRequire;

    @ApiModelProperty(value = "包裹的邮编验证")
    private Boolean postCodeValifition;

    @ApiModelProperty(value = "包裹的邮编匹配")
    private Boolean postCodeMatch;

    @ApiModelProperty(value = "包裹的收件人必须填写")
    private Boolean recipientRequire;

    @ApiModelProperty(value = "包裹的收件人长度限制")
    private Integer recipientLength;

    @ApiModelProperty(value = "包裹的电话号码必须填写")
    private Boolean phoneRequire;

    @ApiModelProperty(value = "包裹的电话号码长度限制")
    private Integer phoneLength;

    @ApiModelProperty(value = "包裹的电话号码验证")
    private Boolean phoneValifition;

    @ApiModelProperty(value = "包裹的电子邮件Email必须填写")
    private Boolean emailRequire;

    @ApiModelProperty(value = "包裹的电子邮件Email长度限制")
    private Integer emailLength;

    @ApiModelProperty(value = "包裹的州最小长度限制")
    private Integer minStateLength;

    @ApiModelProperty(value = "包裹的最小城市长度限制")
    private Integer minCityLength;

    @ApiModelProperty(value = "服务的最小地址1长度限制（Address1）")
    private Integer minAddressStreet1Length;

    @ApiModelProperty(value = "服务的最小地址2长度限制（Address2）")
    private Integer minAddressStreet2Length;

    @ApiModelProperty(value = "服务的最小地址3长度限制（Address3）")
    private Integer minAddressStreet3Length;

    @ApiModelProperty(value = "服务的最小地址总长度限制(Address1+Address2+Address3)")
    private Integer minAddressLength;

    @ApiModelProperty(value = "包裹的最小收件人长度限制")
    private Integer minRecipientLength;

    @ApiModelProperty(value = "包裹的最小电话号码长度限制")
    private Integer minPhoneLength;

    @ApiModelProperty(value = "包裹的最小电子邮件Email长度限制")
    private Integer minEmailLength;

    @ApiModelProperty(value = "邮编最小长度限制")
    private Integer minPostCodeLength;

    @ApiModelProperty(value = "邮编长度限制")
    private Integer postCodeLength;

    @ApiModelProperty(value = "包裹的最小电话号码数字长度限制")
    private Integer minPhoneNumberLength;

    @ApiModelProperty(value = "包裹的最大电话号码数字长度限制")
    private Integer maxPhoneNumberLength;

    @ApiModelProperty(value = "最小申报价值")
    private BigDecimal minDeclaredValue;

    @ApiModelProperty(value = "最大申报价值")
    private BigDecimal maxDeclaredValue;

    @ApiModelProperty(value = "一句话卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "小图URL")
    private String productThumbnail;

    @ApiModelProperty(value = "大图URL")
    private String productPicture;

    @ApiModelProperty(value = "价格说明")
    private String pricingRemark;

    @ApiModelProperty(value = "可带电")
    private Boolean withBattery;

    @ApiModelProperty(value = "時效最小值（天）")
    private Integer limitationDayMin;

    @ApiModelProperty(value = "時效最大值（天）")
    private Integer limitationDayMax;

    @ApiModelProperty(value = "重量加成配置")
    private WeightAddition weightAddition;

    @ApiModelProperty(value = "黑名单")
    private List<String> blackList;

    @ApiModelProperty(value = "白名单")
    private List<String> whiteList;

    @ApiModelProperty(value = "Tag黑名单")
    private List<String> tagBlackList;

    @ApiModelProperty(value = "Tag白名单")
    private String tagWhiteList;

    @ApiModelProperty(value = "一票多件包裹數量限制")
    private Integer multiPackageLength;

    @ApiModelProperty(value = "发货规则")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则")
    private String packingRule;

}
