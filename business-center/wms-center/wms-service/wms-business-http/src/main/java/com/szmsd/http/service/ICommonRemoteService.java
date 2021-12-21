package com.szmsd.http.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.http.domain.CommonRemote;
import com.szmsd.http.dto.HttpRequestDto;
import com.szmsd.http.dto.HttpRequestSyncDTO;
import com.szmsd.http.enums.RemoteConstant;

/**
 * <p>
 * 扫描执行任务 服务类
 * </p>
 *
 * @author huanggaosheng
 * @since 2021-11-10
 */
public interface ICommonRemoteService extends IService<CommonRemote> {


    void doTask(CommonRemote oneTask);

    CommonRemote getOneTask(Integer id, RemoteConstant.RemoteTypeEnum remoteTypeEnum);

    void insertRmiOne(HttpRequestSyncDTO dto);
}

