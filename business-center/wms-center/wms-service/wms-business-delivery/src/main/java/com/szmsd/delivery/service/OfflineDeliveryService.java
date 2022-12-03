package com.szmsd.delivery.service;

import com.szmsd.common.core.domain.R;
import org.springframework.web.multipart.MultipartFile;

public interface OfflineDeliveryService {


    R importExcel(MultipartFile file);
}
