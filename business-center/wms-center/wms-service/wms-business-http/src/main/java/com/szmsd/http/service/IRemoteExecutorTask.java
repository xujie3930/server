package com.szmsd.http.service;

/**
 * @ClassName: IRemoteExecutorTask
 * @Description:
 * @Author: 11
 * @Date: 2021-11-20 14:54
 */
public interface IRemoteExecutorTask {

    void executeTask();

    void syncFinishScanDate();

}
