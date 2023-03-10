package com.szmsd.track.dto;

import com.szmsd.bas.plugin.vo.BasSubWrapperVO;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class TrackMainDocCommonDto extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "状态数量集合")
    private Map<String, Integer> delTrackStateDto;

    @ApiModelProperty(value = "物流数据集合")
    private List<TrackDetailDocDto> trackingList;

    @ApiModelProperty(value = "状态类型")
    private List<BasSubWrapperVO> delTrackStateTypeList;

}
