package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.putinstorage.domain.dto.AttachmentFileDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: RefundRequestQueryDTO
 * @Description: 退款申请查询条件
 * @Author: 11
 * @Date: 2021-08-13 11:46
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "退款申请新增修改对象")
public class RefundRequestListDTO {

    @NotEmpty(message = "退款申请不能为空")
    @ApiModelProperty(value = "退款申请")
    private List<RefundRequestDTO> refundRequestList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
