package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.BasFile;
import com.szmsd.delivery.domain.BasFileDao;

import java.util.List;

public interface BasFileService {
    List<BasFile>  selectBasFile(BasFileDao basFile);
}
