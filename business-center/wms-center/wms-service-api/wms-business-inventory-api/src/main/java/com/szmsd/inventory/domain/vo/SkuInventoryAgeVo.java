package com.szmsd.inventory.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @ClassName: SkuInventoryAgeVo
 * @Description: sku库龄
 * @Author: 11
 * @Date: 2021-08-06 9:22
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "sku库龄")
public class SkuInventoryAgeVo {
    @ApiModelProperty(value = "时间维度")
    private ChronoUnit timeDimension = ChronoUnit.WEEKS;
    @ApiModelProperty(value = "时间维度描述", example = "1", hidden = true)
    private String timeDimensionDesc;
    @ApiModelProperty(value = "时间维度描述-展示", example = "1 WEEK", hidden = true)
    private String timeDimensionDescShow;
    @ApiModelProperty(value = "库龄", example = "1")
    private long storageAge;
    @ApiModelProperty(value = "sku", example = "a")
    private String sku;
    @ApiModelProperty(value = "仓库code", example = "a")
    private String warehouseCode;
    @ApiModelProperty(value = "数量", example = "1")
    private Integer num;
    @ApiModelProperty(value = "处理类型", example = "1")
    private String type;
    @ApiModelProperty(value = "创建时间", example = "1")
    private Date createTime;
    @ApiModelProperty(value = "创建时间", example = "1")
    private String createTimeStr;

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
        LocalDate parse1 = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(createTime));
        this.createTimeStr = parse1 + "";
        this.storageAge = calculateStorageAge(parse1, this.timeDimension);
        this.timeDimensionDesc = getDayOfWeek(parse1);
        this.timeDimensionDescShow = this.timeDimensionDesc + " " + this.timeDimension;
    }

    private static long calculateStorageAge(LocalDate date, ChronoUnit chronoUnit) {
        LocalDate now = LocalDate.now();
        return date.until(now, chronoUnit);
    }

    private static String getDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().getValue() + "";
    }
}
