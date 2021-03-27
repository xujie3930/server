package com.szmsd.http.vo.returnex;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.vo.ResponseVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 创建退件预报返回结果
 *
 * @author 11
 * @date 2021/3/27 10:30
 */
@Data
@ApiModel(value = "CreateExpectedRespVO", description = "创建退件预报返回结果")
public class CreateExpectedRespVO extends ResponseVO {
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
