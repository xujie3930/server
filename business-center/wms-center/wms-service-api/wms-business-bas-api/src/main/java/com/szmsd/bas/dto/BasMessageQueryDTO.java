package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasMessage;
import lombok.Data;

@Data
public class BasMessageQueryDTO extends BasMessage {
    private  String[] createTimes;
}
