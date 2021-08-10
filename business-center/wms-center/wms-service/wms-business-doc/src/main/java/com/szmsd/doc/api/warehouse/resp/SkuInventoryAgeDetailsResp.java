package com.szmsd.doc.api.warehouse.resp;

import com.szmsd.inventory.domain.vo.SkuInventoryAgeVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
public class SkuInventoryAgeDetailsResp {
    @ApiModelProperty(value = "时间维度")
    private ChronoUnit timeDimension = ChronoUnit.WEEKS;
    @ApiModelProperty(value = "时间维度描述", example = "1", hidden = true)
    private String timeDimensionDesc;
    @ApiModelProperty(value = "时间维度描述-展示", example = "1 WEEK", hidden = true)
    private String timeDimensionDescShow;
    @ApiModelProperty(value = "库龄", example = "1")
    private long storageAge;
    @ApiModelProperty(value = "数量", example = "1")
    private Integer num;

}
