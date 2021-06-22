package com.szmsd.common.core.web.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class PageVO2<T> {

    @ApiModelProperty(value = "当前页码")
    private Integer pageNumber;

    @ApiModelProperty(value = "页面大小")
    private Integer pageSize;

    @ApiModelProperty(value = "查询记录数")
    private Integer totalRecords;

    @ApiModelProperty(value = "总的页面数")
    private Integer totalPages;

    private List<T> data;

    public static <T> PageVO2<T> empty() {
        return new PageVO2().setData(new ArrayList()).setTotalRecords(0);
    }

}
