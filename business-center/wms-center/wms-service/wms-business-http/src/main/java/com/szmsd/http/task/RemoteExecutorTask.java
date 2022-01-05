package com.szmsd.http.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.http.config.CkThreadPool;
import com.szmsd.http.domain.CommonRemote;
import com.szmsd.http.domain.CommonScanFinish;
import com.szmsd.http.mapper.CommonScanMapper;
import com.szmsd.http.service.ICommonRemoteFinishService;
import com.szmsd.http.service.ICommonRemoteService;
import com.szmsd.http.service.IRemoteExecutorTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.szmsd.http.enums.RemoteConstant.RemoteTypeEnum;

/**
 * @ClassName: RemoteExecutorTask
 * @Description: 扫描任务执行器定时任务
 * 每个扫描类型只能同时有一个任务跑
 * @Author: 11
 * @Date: 2021-11-20 11:02
 */
@Slf4j
@Component
public class RemoteExecutorTask implements IRemoteExecutorTask {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource
    private ICommonRemoteService iCommonRemoteService;
    @Resource
    private CommonScanMapper commonScanMapper;
    @Resource
    private ICommonRemoteFinishService commonScanFinishService;

    @Value("${processDay:10}")
    private Integer processDay;
    @Value("${syncTotal:100}")
    private Integer syncTotal;
    @Resource
    private CkThreadPool ckThreadPool;


    private final static String PREFIX = "HTTP:Remote:";
    /**
     * 加载扫描类型
     */
    private final static String SCAN_TYPE_KEY = PREFIX + "remoteSourceKey";

    /**
     * 正在运行且超时的任务
     */
    private final static String RUNNING_KEY = PREFIX + "RUNNING";

    /**
     * 类型对应的缓存id
     */
    private final static String SCAN_TYPE_ID_KEY = PREFIX + "remoteSourceIdKey";

    @Resource
    private RedissonClient redissonClient;
    private static final Long LOCK_TIME = 30L;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    /**
     * 初始化执行任务类型
     * 及
     * 任务类型对应的缓存优化id
     */
    @PostConstruct
    public void reInit() {
        RLock lock = redissonClient.getLock("【RmiSyncTask】Task#reInit" + LocalDate.now());
        try {
            if (lock.tryLock(LOCK_TIME, TIME_UNIT)) {
                redisTemplate.delete(RUNNING_KEY);
                Boolean delete = redisTemplate.delete(SCAN_TYPE_KEY);
                Boolean hasKey = redisTemplate.hasKey(SCAN_TYPE_KEY);
                if (null != hasKey && !hasKey) {
                    log.info("---【RmiSyncTask】初始化参数---");
                    Arrays.stream(RemoteTypeEnum.values()).forEach(x -> {
                        if (x.getTypeCode() == -1) return;
                        // 后期改任务数在这里添加多个队列任务
                        String typeCodeStr = x.getTypeCode() + "";
                        redisTemplate.boundListOps(SCAN_TYPE_KEY).leftPush(typeCodeStr);
                        redisTemplate.boundHashOps(SCAN_TYPE_ID_KEY).put(typeCodeStr, "1");
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("---【RmiSyncTask】 初始化参数完成 ---");
            }
        }
    }

    /**
     * 每3分钟激活一种类型
     */
//    @Scheduled(cron = "0 0/1 * * * ?")
//    @Scheduled(fixedDelay = 1000 * 60)
    @Scheduled(fixedDelay = 1000 * 1)
    @Override
    public void executeTask() {
        // 取出一个类型
        String typeCode = null;
        if (StringUtils.isNotBlank(typeCode = redisTemplate.boundListOps(SCAN_TYPE_KEY).rightPop())) {
            String finalTypeCode = typeCode;

            ckThreadPool.execute(() -> {
                CommonRemote oneTask = null;
                try {
//                    log.info("扫描执行-开始扫描执行任务类型 - {}", finalTypeCode);
                    RemoteTypeEnum scanEnumByType = RemoteTypeEnum.getScanEnumByType(Integer.parseInt(finalTypeCode));
                    Object typeKeyObj = null;
                    if (Objects.nonNull(typeKeyObj = redisTemplate.boundHashOps(SCAN_TYPE_ID_KEY).get(finalTypeCode))) {
                        int typeId = Integer.parseInt(typeKeyObj.toString());

                        while ((oneTask = iCommonRemoteService.getOneTask(typeId, scanEnumByType)) != null) {
                            log.info("扫描执行-开始执行--{}", oneTask.getId());
                            // 设置下一个id;
                            typeId = oneTask.getId();
                            CommonRemote finalOneTask = oneTask;
                            String id = finalOneTask.getId() + "";
                            Object o = redisTemplate.opsForHash().get(RUNNING_KEY, id);
                            if (Objects.nonNull(o)) {
                                log.info("任务正在执行列表中，不在执行：{}", id);
                                typeId = typeId + 1;
                                continue;
                            }
                            // 超时则直接跳过 执行下一个
                            Future<?> submit = ckThreadPool.submit(() -> {
                                try {
                                    iCommonRemoteService.doTask(finalOneTask);
                                } finally {
                                    Long delete = redisTemplate.opsForHash().delete(RUNNING_KEY, id);
                                    log.info("任务结束删除执行任务：{}--{}", id, delete);
                                }
                            });

                            try {
                                submit.get(3, TimeUnit.MINUTES);
                            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                                e.printStackTrace();
                                log.info("请求执行超时【3分钟】跳过执行--{}", finalOneTask.getId());
                                redisTemplate.boundHashOps(RUNNING_KEY).put(finalOneTask.getId() + "", LocalDateTime.now().toString());
                            }
                            log.info("扫描执行-执行完成--{}--typeId---{}", oneTask.getId(), typeId);
                        }
                        // 重置redis
                        // redisTemplate.boundHashOps(SCAN_TYPE_ID_KEY).put(finalTypeCode, "1");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("扫描执行-扫描任务执行异常：{}", finalTypeCode, e);
                } finally {
                    if (StringUtils.isNotBlank(finalTypeCode)) {
                        log.info("扫描执行-finally 执行完成--{}", finalTypeCode);
                        redisTemplate.boundListOps(SCAN_TYPE_KEY).leftPush(finalTypeCode);
                    }
                    if (Objects.nonNull(oneTask)) {
                        log.info("扫描执行-finally 执行完成--{}", oneTask.getId());
                        redisTemplate.boundHashOps(SCAN_TYPE_ID_KEY).put(finalTypeCode, oneTask.getId() + "");
                    }
                }
            });
        }
    }

    /**
     * 同步换箱扫描任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncFinishScanDate() {
        RLock lock = redissonClient.getLock("RemoteExecutorTask#syncFinishScanDate" + LocalDate.now());
        try {
            if (lock.tryLock(LOCK_TIME, TIME_UNIT)) {
                LocalDate now = LocalDate.now();
                LocalDate localDate = now.minusDays(processDay);
                List<CommonRemote> commonRemotes;
                while (CollectionUtils.isNotEmpty(commonRemotes = commonScanMapper.selectList(Wrappers.<CommonRemote>lambdaQuery().lt(BaseEntity::getCreateTime, localDate).last("LIMIT " + syncTotal)))) {
                    log.info("syncFinishScanDate-开始同步数据：{}天前：{}---{}条", processDay, localDate, commonRemotes.size());
                    CopyOnWriteArrayList<Integer> deleteIdList = new CopyOnWriteArrayList<>();
                    List<CommonScanFinish> collect = commonRemotes.stream().map(x -> {
                        CommonScanFinish commonScanFinish = new CommonScanFinish();
                        BeanUtils.copyProperties(x, commonScanFinish);
                        deleteIdList.add(x.getId());
                        return commonScanFinish;
                    }).collect(Collectors.toList());
                    commonScanFinishService.saveBatch(collect);
                    log.info("syncFinishScanDate-开始同步数据完成：{}---{}条开始删除数据--", localDate, collect.size());

                    int delete = commonScanMapper.delete(Wrappers.<CommonRemote>lambdaQuery().lt(BaseEntity::getCreateTime, localDate).in(CommonRemote::getId, deleteIdList));
                    log.info("syncFinishScanDate-删除数据完成：{}---{}条--", localDate, delete);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("syncFinishScanDate-数据同步任务执行异常：", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("syncFinishScanDate-删除数据执行完成解锁---");
            }
        }

    }
}
