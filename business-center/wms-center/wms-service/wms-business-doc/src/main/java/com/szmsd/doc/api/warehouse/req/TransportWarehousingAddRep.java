package com.szmsd.doc.api.warehouse.req;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.doc.api.SwaggerDictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName: TransportWarehousingAddDTO
 * @Description: zhuan
 * @Author: 11
 * @Date: 2021-04-27 18:01
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "转运入库新增对象")
public class TransportWarehousingAddRep {

    @Size(max = 50, message = "目标仓库仅支持50字符")
    @ApiModelProperty(value = "目标仓库编码", example = "NJ",hidden = true, required = true)
    @Excel(name = "目标仓库code")
    private String warehouseCode;

    @ApiModelProperty(value = "入库方式编码", hidden = true)
    private String warehouseMethodCode = "055005";

    public void setWarehouseMethodCode(String warehouseMethodCode) {
    }

    @SwaggerDictionary(dicCode = "053")
    @NotBlank(message = "送货方式编码不能为空")
    @Size(max = 30, message = "送货方式编码仅支持0-30字符")
    @ApiModelProperty(value = "送货方式编码 (0-30]", example = "053001", required = true)
    private String deliveryWay;

    @Size(max = 200, message = "送货单号仅支持0-200字符")
    @ApiModelProperty(value = "送货单号（快递单号） 可支持多个", example = "1121,22")
    private String deliveryNo;

    @Size(max = 30, message = "VAT 仅支持 0-30 字符")
    @ApiModelProperty(value = "VAT")
    private String vat;

    @SwaggerDictionary(dicCode = "056")
    @NotBlank(message = "类别编码不能为空")
    @Size(max = 30, message = "类别编码仅支持0-30字符")
    @ApiModelProperty(value = "类别编码 (0-30]", example = "056001", required = true)
    private String warehouseCategoryCode;

    @Length(max = 30, message = "转运入库出库单号仅支持30条")
    @NotEmpty(message = "转运入库出库单号不能为空")
    @ApiModelProperty(value = "转运单列表 - 出库单号", example = "[\"CKCNYWO721082500000008\"]", required = true)
    List<String> transferNoList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}

