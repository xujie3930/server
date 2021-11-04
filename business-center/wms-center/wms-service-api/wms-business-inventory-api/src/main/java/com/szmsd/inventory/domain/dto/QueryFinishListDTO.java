package com.szmsd.inventory.domain.dto;

import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName: QueryFinishListDTO
 * @Description:
 * @Author: 11
 * @Date: 2021-10-28 11:11
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "查询入参")
public class QueryFinishListDTO extends QueryDto {

    @ApiModelProperty(value = "no")
    private String no;

    @ApiModelProperty(value = "cusCode")
    private String cusCode;

    public void setNo(String no) {
        this.no = no;
        this.noList = StringToolkit.getCodeByArray(no);
    }

    @ApiModelProperty(value = "noList", hidden = true)
    private List<String> noList;
    @NotNull
    @ApiModelProperty(value = "类型：0:入库。1：出库默认：0")
    private Integer type = 0;

}
