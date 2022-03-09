package com.szmsd.pack.service.impl;

import com.szmsd.pack.constant.PackageCollectionOperationRecordConstants;
import com.szmsd.pack.domain.PackageCollection;

import java.util.HashSet;
import java.util.Set;

/**
 * 揽收业务逻辑上下文
 */
public class PackageCollectionContext {

    private PackageCollection packageCollection;

    private Type type;

    private boolean hasDetail;

    private Set<PackageCollectionOperationRecordConstants.Type> undoType;

    public PackageCollectionContext() {
    }

    public PackageCollectionContext(PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }

    public PackageCollectionContext(PackageCollection packageCollection, Type type) {
        this.packageCollection = packageCollection;
        this.type = type;
    }

    public PackageCollectionContext(PackageCollection packageCollection, Type type, boolean hasDetail) {
        this.packageCollection = packageCollection;
        this.type = type;
        this.hasDetail = hasDetail;
    }

    public PackageCollection getPackageCollection() {
        return packageCollection;
    }

    public void setPackageCollection(PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isHasDetail() {
        return hasDetail;
    }

    public void setHasDetail(boolean hasDetail) {
        this.hasDetail = hasDetail;
    }

    public Set<PackageCollectionOperationRecordConstants.Type> getUndoType() {
        return undoType;
    }

    public void setUndoType(Set<PackageCollectionOperationRecordConstants.Type> undoType) {
        this.undoType = undoType;
    }

    public void addUndoType(PackageCollectionOperationRecordConstants.Type type) {
        if (null == this.undoType) {
            this.undoType = new HashSet<>();
        }
        this.undoType.add(type);
    }

    public enum Type {
        /**
         * 单据取消
         */
        CANCEL,
        /**
         * 创建入库单
         */
        CREATE_RECEIVER
    }
}
