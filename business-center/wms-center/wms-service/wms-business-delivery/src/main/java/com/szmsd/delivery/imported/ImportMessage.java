package com.szmsd.delivery.imported;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 16:57
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ImportMessage", description = "ImportMessage对象")
public class ImportMessage implements Serializable {

    @ApiModelProperty(value = "行号")
    private Integer rowIndex;

    @ApiModelProperty(value = "列号")
    private Integer columnIndex;

    @ApiModelProperty(value = "值")
    private String value;

    @ApiModelProperty(value = "信息")
    private String message;

    /**
     * 构建数据
     *
     * @param message message
     * @return List<ImportMessage>
     */
    public static List<ImportMessage> build(String message) {
        List<ImportMessage> list = new ArrayList<>();
        list.add(new ImportMessage(1, 1, "", message));
        return list;
    }

}
