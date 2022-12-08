package com.szmsd.delivery.service;

import com.szmsd.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

/**
 * 二次计费
 */
public interface ChargeService {

    /**
     * 二次计费导入
     * @param file
     * @return
     */
    R importExcel(MultipartFile file);

    /**
     * 二次计费
     */
    R doSecondCharge();
}
