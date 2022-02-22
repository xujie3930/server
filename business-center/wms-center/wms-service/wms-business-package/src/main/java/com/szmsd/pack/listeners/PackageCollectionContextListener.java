package com.szmsd.pack.listeners;

import com.szmsd.pack.domain.PackageCollection;
import com.szmsd.pack.events.PackageCollectionContextEvent;
import com.szmsd.pack.service.IPackageCollectionService;
import com.szmsd.pack.service.impl.PackageCollectionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class PackageCollectionContextListener {

    @Autowired
    private IPackageCollectionService packageCollectionService;

    @Async
    @EventListener
    public void onApplicationEvent(PackageCollectionContextEvent event) {
        Object source = event.getSource();
        if (source instanceof PackageCollectionContext) {
            PackageCollectionContext packageCollectionContext = (PackageCollectionContext) source;
            PackageCollection packageCollection = packageCollectionContext.getPackageCollection();
            if (PackageCollectionContext.Type.CANCEL.equals(packageCollectionContext.getType())) {
                // 处理单据取消的逻辑
                this.packageCollectionService.notRecordCancel(packageCollection);
            } else if (PackageCollectionContext.Type.CREATE_RECEIVER.equals(packageCollectionContext.getType())) {
                // 创建入库单
            }
        }
    }
}
