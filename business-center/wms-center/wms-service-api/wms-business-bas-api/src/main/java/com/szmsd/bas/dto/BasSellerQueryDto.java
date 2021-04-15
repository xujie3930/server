package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSeller;
import lombok.Data;

@Data
public class BasSellerQueryDto extends BasSeller {
    private int pageNum;
    private int pageSize;
}
