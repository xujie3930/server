package com.szmsd.delivery.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "", description = "DelTrackCommonDto对象")
public class DelTrackMainCommonDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态数量集合")
    private Map<String, Integer> delTrackStateDto;

    @ApiModelProperty(value = "物流数据集合")
    private List<List<DelTrackCommonDto>> trackingList;

    @ApiModelProperty(value = "状态类型")
    private List<BasSubWrapperVO> delTrackStateTypeList;

}
