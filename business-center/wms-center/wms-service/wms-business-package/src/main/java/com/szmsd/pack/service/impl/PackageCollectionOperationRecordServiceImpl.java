package com.szmsd.pack.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.pack.domain.PackageCollectionOperationRecord;
import com.szmsd.pack.mapper.PackageCollectionOperationRecordMapper;
import com.szmsd.pack.service.IPackageCollectionOperationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PackageCollectionOperationRecordServiceImpl extends ServiceImpl<PackageCollectionOperationRecordMapper, PackageCollectionOperationRecord> implements IPackageCollectionOperationRecordService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void add(String collectionNo, String type) {
        PackageCollectionOperationRecord record = new PackageCollectionOperationRecord();
        record.setCollectionNo(collectionNo);
        record.setType(type);
        super.save(record);
    }
}
