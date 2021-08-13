package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: ConfirmOperationDTO
 * @Description: 系统确认操作
 * @Author: 11
 * @Date: 2021-08-13 16:44
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "系统确认操作")
public class ConfirmOperationDTO {
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
