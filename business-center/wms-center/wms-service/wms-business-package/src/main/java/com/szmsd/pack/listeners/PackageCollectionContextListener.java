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
            PackageCollection packageCollection = ((PackageCollectionContext) source).getPackageCollection();
            this.packageCollectionService.notRecordCancel(packageCollection);
        }
    }
}
