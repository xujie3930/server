package com.szmsd.returnex.domain.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "DocumentVO", description = "单据统计")
public class DocumentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "客户代号")
    private String cusCode;

    @ApiModelProperty(value = "单据类型")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.HOME_DOCUMENT_TYPE)
    private String documentType;

    @ApiModelProperty(value = "数量")
    private String count;

}
