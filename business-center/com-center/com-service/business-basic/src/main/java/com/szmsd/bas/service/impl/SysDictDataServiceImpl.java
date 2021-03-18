package com.szmsd.bas.service.impl;

import com.szmsd.bas.dao.SysDictDataMapper;
import com.szmsd.bas.service.ISysDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liulei
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {
    @Autowired
    SysDictDataMapper sysDictDataMapper;

}
