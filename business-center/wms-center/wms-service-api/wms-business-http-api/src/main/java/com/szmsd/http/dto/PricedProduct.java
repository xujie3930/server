package com.szmsd.http.dto;

import com.szmsd.http.vo.Operation;
import com.szmsd.http.vo.PricedProductSheet;
import com.szmsd.http.vo.WeightAddition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 18:57
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PricedProduct implements Serializable {

    // 产品代码
    private String code;
    // 产品名称
    private String name;
    // 产品类型
    private String type;
    // 产品分类
    private String category;
    // 产品服务
    private String service;
    // 挂号逾期天数
    private Integer overdueDay;
    // 支持发货类型
    private List<String> shipmentTypeSupported;
    // 是否支持一票多件发货
    private Boolean isMultiPackageShipment;
    // 挂号服务
    private String logisticsRouteId;
    // 终端运输商
    private String terminalCarrier;
    // 轨迹官网地址
    private String trackWebsite;
    // 子产品
    private List<String> subProducts;
    // 报价表
    private List<PricedProductSheet> sheets;
    // 是否生效
    private Boolean isValid;
    // 是否挂号服务
    private Boolean isTracking;
    // 是否可查询
    private Boolean isShow;
    // 是否可下单
    private Boolean inService;
    // 父产品代码
    private String parentProduct;
    // 挂号获取方式
    private String trackingAcquireType;
    // 包裹的最小电话号码数字长度限制
    private Integer minPhoneNumberLength;
    // 包裹的最大电话号码数字长度限制
    private Integer maxPhoneNumberLength;
    // 最小申报价值
    private Double minDeclaredValue;
    // 最大申报价值
    private Double maxDeclaredValue;
    // 核重后挂号获取方式
    private String trackingAcquireTypeAfterWeightConfirm;
    // 必须导入挂号
    private Boolean forceImportTracking;
    // 客户能否导入挂号
    private Boolean canImportTracking;
    // 打印地址标签（是否必须存在挂号）
    private Boolean forcePrintLableWithTracking;
    // 包裹的州必须填写
    private Boolean stateRequire;
    // 包裹的州长度限制
    private Integer stateLength;
    // 包裹的州验证
    private Boolean stateValifition;
    // 包裹的城市必须填写
    private Boolean cityRequire;
    // 包裹的城市长度限制
    private Integer cityLength;
    // 服务的地址1长度限制（Address1）
    private Integer addressStreet1Length;
    // 服务的地址2长度限制（Address2）
    private Integer addressStreet2Length;
    // 服务的地址3长度限制（Address3）
    private Integer addressStreet3Length;
    // 服务的地址长度限制(Address1+Address2+Address3)
    private Integer addressLength;
    // 包裹的邮编必须填写
    private Boolean postCodeRequire;
    // 包裹的邮编验证
    private Boolean postCodeValifition;
    // 包裹的邮编匹配
    private Boolean postCodeMatch;
    // 包裹的收件人必须填写
    private Boolean recipientRequire;
    // 包裹的收件人长度限制
    private Integer recipientLength;
    // 包裹的电话号码必须填写
    private Boolean phoneRequire;
    // 包裹的电话号码长度限制
    private Integer phoneLength;
    // 包裹的电话号码验证
    private Boolean phoneValifition;
    // 包裹的电子邮件Email必须填写
    private Boolean emailRequire;
    // 包裹的电子邮件Email长度限制
    private Integer emailLength;
    // 包裹的州最小长度限制
    private Integer minStateLength;
    // 包裹的最小城市长度限制
    private Integer minCityLength;
    // 服务的最小地址1长度限制（Address1）
    private Integer minAddressStreet1Length;
    // 服务的最小地址2长度限制（Address2）
    private Integer minAddressStreet2Length;
    // 服务的最小地址3长度限制（Address3）
    private Integer minAddressStreet3Length;
    // 服务的最小地址总长度限制(Address1+Address2+Address3)
    private Integer minAddressLength;
    // 包裹的最小收件人长度限制
    private Integer minRecipientLength;
    // 包裹的最小电话号码长度限制
    private Integer minPhoneLength;
    // 包裹的最小电子邮件Email长度限制
    private Integer minEmailLength;
    // 邮编最小长度限制
    private Integer minPostCodeLength;
    // 邮编长度限制
    private Integer postCodeLength;
    // 一句话卖点
    private String sellingPoint;
    // 小图URL
    private String productThumbnail;
    // 大图URL
    private String productPicture;
    // 价格说明
    private String pricingRemark;
    // 可带电
    private Boolean withBattery;
    // 時效最小值（天）
    private Integer limitationDayMin;
    // 時效最大值（天）
    private Integer limitationDayMax;
    // 包裹申报名称中文名是否必须的
    private Boolean isDeclareChineseNameRequire;
    // 黑名单
    private List<String> blackList;
    // 白名单
    private List<String> whiteList;
    // Tag黑名单
    private List<String> tagBlackList;
    // Tag白名单
    private List<String> tagWhiteList;
    // 重量加成配置
    private WeightAddition weightAddition;
    private Operation creation;
    private Operation lastModifyOperation;
    // 一票多件包裹數量限制
    private Integer multiPackageLength;
    private String avaibleSheets;
}
