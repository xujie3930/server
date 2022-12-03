package com.szmsd.delivery.service;

import com.szmsd.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

public interface OfflineDeliveryService {

    /**
     * 导入线下出库单
     * @param file
     * @return
     */
    R importExcel(MultipartFile file);


    /**
     * 处理线下出库单
     * @return
     */
    R dealOfflineDelivery();

}
