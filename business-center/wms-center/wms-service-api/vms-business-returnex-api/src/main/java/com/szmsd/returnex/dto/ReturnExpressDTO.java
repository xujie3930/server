package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @ClassName: ReturnExpressDTO
 * @Description: 退款单查询对象
 * @Author: 11
 * @Date: 2021/3/26 13:41
 */
@Data
public class ReturnExpressDTO {
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
