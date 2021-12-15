package com.szmsd.putinstorage.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

/**
 * @ClassName: aaa
 * @Description:
 * @Author: 11
 * @Date: 2021-12-15 16:43
 */
@NoArgsConstructor
@Data
public class aaa {

    @NotBlank(message = "订单Id不能为空")
    @Size(max = 50, message = "订单Id最大仅支持50字符")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]*$", message = "订单Id格式不支持")
    @ApiModelProperty(value = "订单Id(第三方系统自定义Id，客户+包裹Id 具有唯一性)", notes = "长度: 0 ~ 50", required = true)
    private String CustomerOrderNo;

    @NotBlank(message = "物流承运商不能为空")
    @Size(max = 30, message = "物流承运商最大仅支持30字符")
    @ApiModelProperty(value = "物流承运商", notes = "长度: 0 ~ 30", required = true)
    private String Carrier;

    @NotEmpty(message = "物流单号不能为空")
    @Size(max = 30, message = "物流单号最大仅支持30字符")
    @ApiModelProperty(value = "物流单号", notes = "长度: 0 ~ 30", required = true)
    private List<String> GoodsIncomingCode;

    @NotNull(message = "货物处理方式不能为空")
    @ApiModelProperty(value = "货物处理方式", notes = "", required = true)
    private GoodsHandleWayEnum GoodsHandleWay;

    @NotNull(message = "仓库代码不能为空")
    @Size(max = 30, message = "仓库代码最大仅支持30字符")
    @ApiModelProperty(value = "仓库代码", notes = "长度: 0 ~ 30", required = true)
    private String HandleWarehouseCode;

    @NotEmpty(message = "货物清单信息不能为空")
    @ApiModelProperty(value = "货物清单信息", notes = "", required = true)
    private List<CustomerManifestItemsDTO> CustomerManifestItems;
    @ApiModelProperty(value = "销售VAT", notes = "长度: 0 ~ 100")
    private String SaleVAT;

    @ApiModelProperty(value = "备注", notes = "长度: 0 ~ 200")
    private String Remark;

    @NoArgsConstructor
    @Data
    public static class CustomerManifestItemsDTO {
        @NotBlank(message = "货物编号（库存编码或其他条码标识）不能为空")
        @ApiModelProperty(value = "货物编号（库存编码或其他条码标识）", notes = "", required = true)
        private String No;

        @ApiModelProperty(value = "上架库存编码（库存编码或其他条码标识）", notes = "")
        private String StorageNo;

        @NotNull(message = "客户填写的申报数量不能为空")
        @ApiModelProperty(value = "客户填写的申报数量", notes = "", required = true)
        private Integer CustomerQuantity;

        @NotBlank(message = "货物描述信息不能为空")
        @ApiModelProperty(value = "货物描述信息", notes = "", required = true)
        private String GoodsDescription;

        @NotBlank(message = "申报名称不能为空")
        @ApiModelProperty(value = "申报名称", notes = "", required = true)
        private String DeclareName;
    }

}

enum GoodsHandleWayEnum {
    /**
     * 点数上架：仓库收到货物后，按产品进行点数，然后上架。为方便管理产品，完成点数后，您需要为每个FBA产品匹配一个SKU。
     */
    CountingAndPutaway,
    /**
     * 换CK1标签上架：仓库收到货物后，先按产品进行点数，然后根据您给各FBA产品匹配的新产品编码或SKU，更换产品标签并上架。
     */
    CK1LabelingAndPutaway,
    /**
     * 弃件销毁：仓库收到货物后，将按当地政策对货物进行销毁。
     * DM这边没用
     */
    CountingAndDestory;
}
