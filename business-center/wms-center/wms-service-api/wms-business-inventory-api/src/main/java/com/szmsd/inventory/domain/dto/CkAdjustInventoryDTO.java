package com.szmsd.inventory.domain.dto;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import com.szmsd.http.config.CkConfig;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.putinstorage.api.dto.CkCreateIncomingOrderDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

import static com.szmsd.common.core.language.enums.LocalLanguageEnum.INVENTORY_RECORD_TYPE_5;

/**
 * @ClassName: CkAdjustInventoryDTO
 * @Description:
 * @Author: 11
 * @Date: 2021-12-21 16:20
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(description = "CK1-创建来货单(入库单)")
public class CkAdjustInventoryDTO {
    @NotBlank(message = "操作流水号不能为空")
    @ApiModelProperty(value = "操作流水号(客户+操作流水号具有唯一性)", required = true)
    private String SerialNo;
    @NotBlank(message = "仓库代码不能为空")
    @ApiModelProperty(value = "仓库代码", required = true)
    private String WarehouseCode;
    @NotBlank(message = "库存编码不能为空")
    @ApiModelProperty(value = "库存编码", required = true)
    private String StorageNo;
    @NotNull(message = "调整数量不能为空")
    @ApiModelProperty(value = "调整数量(正数为调增，负数为调减)", required = true)
    private Integer Quantity;
    @ApiModelProperty(value = "备注", required = true)
    private String Remark;

    public static CkAdjustInventoryDTO createCkAdjustInventoryDTO(InventoryAdjustmentDTO inventoryAdjustmentDTO) {
        String adjustment = inventoryAdjustmentDTO.getAdjustment();
        LocalLanguageEnum localLanguageEnum = LocalLanguageEnum.getLocalLanguageEnum(LocalLanguageTypeEnum.INVENTORY_RECORD_TYPE, adjustment);
        boolean increase = INVENTORY_RECORD_TYPE_5 == localLanguageEnum;
        boolean reduce = LocalLanguageEnum.INVENTORY_RECORD_TYPE_6 == localLanguageEnum;
        Integer quantity = inventoryAdjustmentDTO.getQuantity();
        AssertUtil.isTrue(increase || reduce, "调整类型有误");
        quantity = increase ? quantity : -quantity;

        String sku = inventoryAdjustmentDTO.getSku();
        String warehouseCode = inventoryAdjustmentDTO.getWarehouseCode();
        String sellerCode = inventoryAdjustmentDTO.getSellerCode();

        CkAdjustInventoryDTO ckAdjustInventoryDTO = new CkAdjustInventoryDTO();
        ckAdjustInventoryDTO.setSerialNo(sku);
        ckAdjustInventoryDTO.setWarehouseCode(Ck1DomainPluginUtil.wrapper(warehouseCode));
        ckAdjustInventoryDTO.setStorageNo(CkConfig.genCk1SkuInventoryCode(sellerCode,warehouseCode,sku));

        ckAdjustInventoryDTO.setQuantity(quantity);
        ckAdjustInventoryDTO.setRemark("");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CkAdjustInventoryDTO>> validate = validator.validate(ckAdjustInventoryDTO);
        String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        AssertUtil.isTrue(StringUtils.isBlank(error), "推送CK1-调整库存请求参数异常：" + error);
        return ckAdjustInventoryDTO;
    }
}
