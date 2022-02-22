package com.szmsd.pack.service.impl;

import com.szmsd.pack.domain.PackageCollection;

/**
 * 揽收业务逻辑上下文
 */
public class PackageCollectionContext {

    private PackageCollection packageCollection;

    private Type type;

    public PackageCollectionContext() {
    }

    public PackageCollectionContext(PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }

    public PackageCollectionContext(PackageCollection packageCollection, Type type) {
        this.packageCollection = packageCollection;
        this.type = type;
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

    public enum Type {
        /**
         * 单据取消
         */
        CANCEL,
        /**
         * 创建出库单
         */
        CREATE_RECEIVER
    }
}
