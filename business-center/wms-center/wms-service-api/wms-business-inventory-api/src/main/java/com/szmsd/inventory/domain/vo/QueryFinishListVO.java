package com.szmsd.inventory.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName: QueryFinishListVO
 * @Description:
 * @Author: 11
 * @Date: 2021-10-28 11:13
 */
@Data
@Accessors(chain = true)
@ApiModel( description = "QueryFinishListVO")
public class QueryFinishListVO {
    @ApiModelProperty(value = "no")
    private String no;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
