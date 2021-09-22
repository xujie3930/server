package com.szmsd.doc.api.sku.request;

import com.szmsd.doc.validator.DictionaryPluginConstant;
import com.szmsd.doc.validator.annotation.Dictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: BasePackingQueryReq
 * @Description:
 * @Author: 11
 * @Date: 2021-09-18 15:02
 */
@Data
@ApiModel(description = "包材查询条件")
public class BasePackingQueryReq {

    @Dictionary(message = "仓库信息不存在", type = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN)
    @ApiModelProperty(value = "仓库信息", example = "NJ")
    private String warehouseCode;

}
