package com.szmsd.pack.service.impl;

import com.szmsd.pack.domain.PackageCollection;

/**
 * 揽收业务逻辑上下文
 */
public class PackageCollectionContext {

    private PackageCollection packageCollection;

    public PackageCollectionContext() {
    }

    public PackageCollectionContext(PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }

    public PackageCollection getPackageCollection() {
        return packageCollection;
    }

    public void setPackageCollection(PackageCollection packageCollection) {
        this.packageCollection = packageCollection;
    }
}
