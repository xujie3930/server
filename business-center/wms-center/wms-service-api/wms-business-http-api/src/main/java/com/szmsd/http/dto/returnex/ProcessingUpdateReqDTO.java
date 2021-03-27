package com.szmsd.http.dto.returnex;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @ClassName: ProcessingUpdateReqDTO
 * @Description: 接收用户处理方式 DTO
 * @Author: 11
 * @Date: 2021/3/27 10:23
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(value = "创建退件预报")
public class ProcessingUpdateReqDTO {

    /**
     * 仓库代码 length:1-10
     */
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    /**
     * 退件单号 length:1-50
     */
    @ApiModelProperty(value = "退件单号")
    private String orderNo;

    /**
     * 处理方式
     * 销毁：Destroy
     * 整包上架：PutawayByPackage
     * 拆包检查：OpenAndCheck
     * 按明细上架：PutawayBySku
     */
    @ApiModelProperty(value = "处理方式")
    private String processType;

    /**
     * 处理备注 0-500
     */
    @ApiModelProperty(value = "处理备注")
    private String processRemark;

    /**
     * 明细处理备注
     */
    @ApiModelProperty(value = "明细处理备注")
    private List<ReturnDetail> details;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
