package com.szmsd.putinstorage.api.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.http.config.CkConfig;
import com.szmsd.http.util.Ck1DomainPluginUtil;
import com.szmsd.putinstorage.domain.dto.ReceivingRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: CkPutawayDTO
 * @Description: CK1入库上架
 * @Author: 11
 * @Date: 2021-12-15 14:36
 */
@NoArgsConstructor
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "CK1-sku入库上架推送")
public class CkPutawayDTO {

    @NotBlank(message = "操作流水号不能为空")
    @ApiModelProperty(value = "操作流水号(客户+操作流水号具有唯一性)", notes = "长度:0 ~ 255", required = true)
    private String SerialNo = System.currentTimeMillis() + "";

    @NotBlank(message = "订单Id不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{1,25}$", message = "订单Id不满足规则")
    @ApiModelProperty(value = "订单Id", notes = "长度:0 ~ 25", required = true)
    private String CustomerOrderNo;

    @NotBlank(message = "仓库代码不能为空")
    @ApiModelProperty(value = "仓库代码", notes = "", required = true)
    private String WarehouseCode;

    @Valid
    @ApiModelProperty(value = "上架信息列表", notes = "")
    private List<PutawayListDTO> PutawayList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    /**
     * CK1-sku入库上架推送
     * @return
     */
    public static CkPutawayDTO createCkPutawayDTO(ReceivingRequest receivingRequest,String cusCode) {
        CkPutawayDTO ckPutawayDTO = new CkPutawayDTO();
        String warehouseCode = receivingRequest.getWarehouseCode();
        ckPutawayDTO.setCustomerOrderNo(receivingRequest.getOrderNo());
        ckPutawayDTO.setWarehouseCode(Ck1DomainPluginUtil.wrapper(warehouseCode));

        ArrayList<PutawayListDTO> putawayList = new ArrayList<>();

        PutawayListDTO putawayListDTO = new PutawayListDTO();
        putawayListDTO.setQty(receivingRequest.getQty());
        putawayListDTO.setSku(CkConfig.genCk1SkuInventoryCode(cusCode,warehouseCode,receivingRequest.getSku()));

        putawayList.add(putawayListDTO);

        ckPutawayDTO.setPutawayList(putawayList);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<CkPutawayDTO>> validate = validator.validate(ckPutawayDTO);
        String error = validate.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(","));
        AssertUtil.isTrue(StringUtils.isBlank(error), "推送CK1-SKU入库上架 请求参数异常：" + error);
        return ckPutawayDTO;
    }

}


