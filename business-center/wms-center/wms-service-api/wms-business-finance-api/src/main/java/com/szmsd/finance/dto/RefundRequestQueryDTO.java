package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @ClassName: RefundRequestQueryDTO
 * @Description: 退款申请查询条件
 * @Author: 11
 * @Date: 2021-08-13 11:46
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退款申请查询条件")
public class RefundRequestQueryDTO {

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
