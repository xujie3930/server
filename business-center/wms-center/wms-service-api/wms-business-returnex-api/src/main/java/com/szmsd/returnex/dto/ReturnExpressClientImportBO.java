package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode
@ApiModel("客户端导入处理方式对象")
public class ReturnExpressClientImportBO {

    @NotBlank(message = "出库单号不能为空")
    @ExcelProperty(value = "预报单号", index = 0)
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;


    @ApiModelProperty(value = "处理方式", example = "SF123456")
    private ReturnExpressClientImportBaseDTO baseDTO;
    @ApiModelProperty(value = "sku重新上架", example = "SF123456")
    private List<ReturnExpressClientImportSkuDTO> skuDTO;
    @ApiModelProperty(value = "重派对象")
    private ReturnExpressClientImportDelOutboundDto reassignDTO;


    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
