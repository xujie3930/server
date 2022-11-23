package com.szmsd.bas.service;

import com.szmsd.bas.domain.BasFile;
import com.szmsd.bas.domain.BasFileDao;
import com.szmsd.common.core.domain.R;

import java.util.List;

public interface BasFileService {
    List<BasFile>  selectBasFile(BasFileDao basFile);

    R<List<String>> listmodularName();
}
