package com.szmsd.putinstorage.api.dto;

import com.szmsd.http.config.CkConfig;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @ClassName: CkGenCustomSkuNoDTO
 * @Description: 获取Sku库存编码 创建sku库存编码
 * @Author: 11
 * @Date: 2022-01-04 10:39
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@ApiModel(description = "CK1-获取Sku库存编码-生成自定义库存编码")
public class CkGenCustomSkuNoDTO {

    @NotBlank(message = "仓库代号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,10}$", message = "订单Id格式不支持")
    private String WarehouseCode;

    @ApiModelProperty(value = "Sku", notes = "长度: 0 ~ 100", required = true)
    private String Sku;

    @NotBlank(message = "订单Id不能为空")
    @Size(max = 50, message = "订单Id最大仅支持50字符")
    /**
     * 客户编码+仓库+sku编码
     */
    @ApiModelProperty(value = "自定义库存编码", notes = "长度: 0 ~ 100", required = true)
    private String CustomerStorageNo;

    public static CkGenCustomSkuNoDTO createGenCustomSkuNoDTO(InboundReceiptInfoVO inboundReceiptInfoVO, InboundReceiptDetailVO inboundReceiptDetailVO) {
        CkGenCustomSkuNoDTO ckGenCustomSkuNoDTO = new CkGenCustomSkuNoDTO();
        String sku = inboundReceiptDetailVO.getSku();
        String warehouseCode = inboundReceiptInfoVO.getWarehouseCode();
        String cusCode = inboundReceiptInfoVO.getCusCode();
        ckGenCustomSkuNoDTO.setSku(sku);
        ckGenCustomSkuNoDTO.setCustomerStorageNo(CkConfig.genCk1SkuInventoryCode(cusCode,warehouseCode,sku));
        return ckGenCustomSkuNoDTO;

    }
}
