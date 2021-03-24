package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSeller;
import lombok.Data;

@Data
public class BasSellerSysDto extends BasSeller {
    private Long sysId;
}
