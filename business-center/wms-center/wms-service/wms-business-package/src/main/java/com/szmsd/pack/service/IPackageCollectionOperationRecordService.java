package com.szmsd.pack.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.pack.domain.PackageCollectionOperationRecord;

public interface IPackageCollectionOperationRecordService extends IService<PackageCollectionOperationRecord> {

    void add(String collectionNo, String type);
}
