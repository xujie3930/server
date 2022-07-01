package com.szmsd.http.dto.grade;

import com.szmsd.http.dto.PageDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 取消入库单
 */
@Data
@Accessors(chain = true)
public class GradePageRequest extends PageDTO {

//    /** 客户编号集合 **/
//    private List<String> clientCode;

    @ApiModelProperty("产品名称")
    private String name;
    @ApiModelProperty("产品编号")
    private String productCode;

}
