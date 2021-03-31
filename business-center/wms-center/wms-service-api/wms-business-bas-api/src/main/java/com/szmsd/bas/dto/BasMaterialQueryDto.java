package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasMaterial;
import lombok.Data;

@Data
public class BasMaterialQueryDto extends BasMaterial {
    private String codes;

}
