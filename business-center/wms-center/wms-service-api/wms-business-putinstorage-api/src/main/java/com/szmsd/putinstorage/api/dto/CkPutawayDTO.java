package com.szmsd.putinstorage.api.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

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

}


