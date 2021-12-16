package com.szmsd.putinstorage.api.dto;

import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.utils.StringToolkit;
import com.google.common.collect.Lists;

import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.putinstorage.constant.PutinstorageConstant;
import com.szmsd.putinstorage.domain.vo.InboundReceiptDetailVO;
import com.szmsd.putinstorage.domain.vo.InboundReceiptInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: CkCreateIncomingOrderDTO
 * @Description:
 * @Author: 11
 * @Date: 2021-12-15 16:43
 */

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel(description = "CK1-创建来货单(入库单)")
public class CkCreateIncomingOrderDTO {

    @NotBlank(message = "订单Id不能为空")
    @Size(max = 50, message = "订单Id最大仅支持50字符")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]*$", message = "订单Id格式不支持")
    @ApiModelProperty(value = "订单Id(第三方系统自定义Id，客户+包裹Id 具有唯一性)", notes = "长度: 0 ~ 50", required = true)
    private String CustomerOrderNo;

    @NotBlank(message = "物流承运商不能为空")
    @Size(max = 30, message = "物流承运商最大仅支持30字符")
    @ApiModelProperty(value = "物流承运商", notes = "长度: 0 ~ 30", required = true)
    private String Carrier;

    //    @NotEmpty(message = "物流单号不能为空")
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

    public static CkCreateIncomingOrderDTO createIncomingOrderDTO(InboundReceiptInfoVO detailVO) {
        CkCreateIncomingOrderDTO ckCreateIncomingOrderDTO = new CkCreateIncomingOrderDTO();
        ckCreateIncomingOrderDTO.setCustomerOrderNo(detailVO.getWarehouseNo());
        List<String> strings = StringToolkit.convertToListFromStr(detailVO.getDeliveryNo());
        ckCreateIncomingOrderDTO.setCarrier("Carrier");
        ckCreateIncomingOrderDTO.setGoodsIncomingCode(strings);
        String warehouseMethodCode = detailVO.getWarehouseMethodCode();
        if (PutinstorageConstant.RE_LABEL_ON_SELL.equals(warehouseMethodCode)) {
            //重新贴标上架 055002=CK1LabelingAndPutaway
            ckCreateIncomingOrderDTO.setGoodsHandleWay(GoodsHandleWayEnum.CK1LabelingAndPutaway);
        } else {
            //按点数上架 055001=CountingAndPutaway
            ckCreateIncomingOrderDTO.setGoodsHandleWay(GoodsHandleWayEnum.CountingAndPutaway);
//           if (PutinstorageConstant.SKU_ON_SELL_BY_POINT.equals(warehouseMethodCode)){
//               //按点数上架 055001=CountingAndPutaway
//               ckCreateIncomingOrderDTO.setGoodsHandleWay(GoodsHandleWayEnum.CountingAndPutaway);
//           }
        }

        ckCreateIncomingOrderDTO.setHandleWarehouseCode(Ck1DomainPluginUtil.wrapper(detailVO.getWarehouseCode()));
        ckCreateIncomingOrderDTO.setSaleVAT(detailVO.getVat());
        ckCreateIncomingOrderDTO.setRemark(detailVO.getRemark());
        List<InboundReceiptDetailVO> inboundReceiptDetails = detailVO.getInboundReceiptDetails();

        List<CustomerManifestItemsDTO> ckdetailList = inboundReceiptDetails.stream().map(x -> {
            CustomerManifestItemsDTO detail = new CustomerManifestItemsDTO();
            //
            detail.setNo(x.getSku());
            detail.setStorageNo(x.getSku());
            detail.setCustomerQuantity(x.getDeclareQty());
            detail.setGoodsDescription(x.getSkuName());
            detail.setDeclareName(x.getSkuName());

            return detail;
        }).collect(Collectors.toList());

        ckCreateIncomingOrderDTO.setCustomerManifestItems(ckdetailList);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CkCreateIncomingOrderDTO>> validate = validator.validate(ckCreateIncomingOrderDTO);
        String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        AssertUtil.isTrue(StringUtils.isBlank(error), "推送CK1-创建来货单(入库单)请求参数异常：" + error);
        return ckCreateIncomingOrderDTO;
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
