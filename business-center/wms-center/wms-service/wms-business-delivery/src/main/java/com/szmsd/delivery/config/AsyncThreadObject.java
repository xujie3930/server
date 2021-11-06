package com.szmsd.delivery.config;

import org.slf4j.MDC;

import java.io.Serializable;

public class AsyncThreadObject implements Serializable {

    private final String tid;
    private final long threadId;

    public AsyncThreadObject() {
        this(MDC.get("TID"), Thread.currentThread().getId());
    }

    public AsyncThreadObject(String tid, long threadId) {
        this.tid = tid;
        this.threadId = threadId;
    }

    public String getTid() {
        return tid;
    }

    public long getThreadId() {
        return threadId;
    }

    public boolean isAsyncThread() {
        long id = Thread.currentThread().getId();
        // id == threadId 是同一个线程
        // id != threadId 是不同的线程
        return id == this.threadId;
    }

    public void loadTid() {
        MDC.put("TID", this.tid);
    }

    public void unloadTid() {
        MDC.remove("TID");
    }
}
