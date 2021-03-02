package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasCode;
import com.szmsd.bas.domain.BasCodeDto;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-29 9:33
 * @Description
 */
public interface BasCodeService {

    /**
     * 生成系统唯一订单号
     * @param basCodeDto
     * @return
     */
    R createCode(BasCodeDto basCodeDto);

    int saveBasCode(BasCode basCode);

    int deleteBasCode(List<BasCode> list);
}
