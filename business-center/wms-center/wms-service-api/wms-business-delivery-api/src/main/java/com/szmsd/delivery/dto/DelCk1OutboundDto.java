package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "DelCk1OutboundDto", description = "DelCk1OutboundDto对象")
public class DelCk1OutboundDto implements Serializable {

    // 仓库Id
    private String WarehouseId;
    // 出库包裹信息
    private PackageDTO Package;
    // 备注
    private String Remark;
    // 发货规则，用于客户自提指定发货规则或者carrier
    private String Channel;
    // 是否稍后提审，默认是false
    private Boolean SubmitLater;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PackageDTO {
        // 包裹Id(第三方系统自定义Id，客户+包裹Id 具有唯一性)
        private String PackageId;
        // 发货服务代码
        private String ServiceCode;
        // 收货地址，联系人
        private ShipToAddressDTO ShipToAddress;
        // SKU列表
        private List<SkusDTO> Skus;
        // 经济运营商(出口)
        private ExportsInfoDTO ExportsInfo;
        // 经济运营商(进口)
        private ImportsInfoDTO ImportsInfo;
        // 售价
        private BigDecimal SellPrice;
        // 售价货币
        private String SellPriceCurrency;
        // 备注
        private String Remark;
        // 销售平台
        private String SalesPlatform;
        // 导入跟踪号
        private String ImportTrackingNumber;
        // Vat税号或者IOSS号码
        private String VatCode;
        // 导入标签（Base64 编码）,允许pdf和png文件
        private String ImportLabel;
        // 包裹Id一致，数据不一致的情况下，是否取消原订单，重新生成新的订单；默认是true
        private Boolean IsCreateNewOrder;

        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class ShipToAddressDTO {
            // 收件人税号或者VAT税号
            private String TaxId;
            // 国家
            private String Country;
            // 省/州
            private String Province;
            // 城市
            private String City;
            // 街道1
            private String Street1;
            // 街道2
            private String Street2;
            // 邮编
            private String Postcode;
            // 联系人
            private String Contact;
            // 联系人(公司)
            private String Company;
            // 电话
            private String Phone;
            // 邮箱
            private String Email;
        }

        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class ExportsInfoDTO {
            private String EoriCode;
            private String Country;
            private String Province;
            private String City;
            private String Street1;
            private String Street2;
            private String Postcode;
            private String Contact;
            private String Company;
            private String Phone;
            private String Email;
        }

        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class ImportsInfoDTO {
            private String EoriCode;
            private String Country;
            private String Province;
            private String City;
            private String Street1;
            private String Street2;
            private String Postcode;
            private String Contact;
            private String Company;
            private String Phone;
            private String Email;
        }

        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class SkusDTO {
            // 商家SKU
            private String Sku;
            // 数量
            private Long Quantity;
            // 商品名称
            private String ProductName;
            //商品单价
            private Double Price;
            // 物品ID
            private String PlatformItemId;
            // 平台交易号
            private String PlatformTransactionId;
            // 商品对应的海关编码
            private String HsCode;
        }
    }
}
