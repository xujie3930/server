package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.domain.OfflineCostImport;

import java.util.List;

public interface OfflineCostImportMapper extends BaseMapper<OfflineCostImport> {

    int saveBatch(List<OfflineCostImport> offlineCostImports);
}
