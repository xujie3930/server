package com.szmsd.returnex.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: ReturnExpressAssignDTO
 * @Description: 无名件指派用户
 * @Author: 11
 * @Date: 2021/3/27 11:36
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel("无名件指派用户DTO")
public class ReturnExpressAssignDTO implements Serializable {

    /**
     * 列表勾选无名件
     */
    @ApiModelProperty(value = "列表勾选无名件")
    private List<Long> ids;

    @StringLength(minLength = 1, maxLength = 50, message = "指派的客户代码异常")
    @NotBlank(message = "指派的用户不能为空")
    @ApiModelProperty(value = "指派客户code")
    private String sellerCode;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
