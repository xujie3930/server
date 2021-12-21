package com.szmsd.http.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.domain.CommonScanFinish;
import com.szmsd.http.mapper.CommonScanFinishMapper;
import com.szmsd.http.service.ICommonRemoteFinishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 扫描执行任务 服务实现类
 * </p>
 *
 * @author huanggaosheng
 * @since 2021-11-10
 */
@Slf4j
@Service
public class CommonRemoteFinishServiceImpl extends ServiceImpl<CommonScanFinishMapper, CommonScanFinish> implements ICommonRemoteFinishService {

}

