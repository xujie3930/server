package com.szmsd.delivery.service;

import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.delivery.domain.BasFile;

import java.util.List;

public interface BasFileService {
    List<BasFile>  selectBasFile(BasFile basFile);
}
