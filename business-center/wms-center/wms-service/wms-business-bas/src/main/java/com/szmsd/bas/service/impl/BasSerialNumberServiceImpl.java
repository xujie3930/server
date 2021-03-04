package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasSerialNumber;
import com.szmsd.bas.enums.SerialNumberTypeEnum;
import com.szmsd.bas.mapper.BasSerialNumberMapper;
import com.szmsd.bas.service.IBasSerialNumberService;
import com.szmsd.common.core.exception.com.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 流水号信息 服务实现类
 * </p>
 *
 * @author gen
 * @since 2020-11-10
 */
@Service
public class BasSerialNumberServiceImpl extends ServiceImpl<BasSerialNumberMapper, BasSerialNumber> implements IBasSerialNumberService {

    private static final ConcurrentMap<String, MessageFormat> MSG_FORMAT_MAP = new ConcurrentHashMap<>();

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 查询流水号信息模块
     *
     * @param id 流水号信息模块ID
     * @return 流水号信息模块
     */
    @Override
    public BasSerialNumber selectBaseSerialNumberById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询流水号信息模块列表
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 流水号信息模块
     */
    @Override
    public List<BasSerialNumber> selectBaseSerialNumberList(BasSerialNumber baseSerialNumber) {
        QueryWrapper<BasSerialNumber> where = new QueryWrapper<BasSerialNumber>();
        where.setEntity(baseSerialNumber);
        return baseMapper.selectList(where);
    }

    /**
     * 新增流水号信息模块
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 结果
     */
    @Override
    public int insertBaseSerialNumber(BasSerialNumber baseSerialNumber) {
        return baseMapper.insert(baseSerialNumber);
    }

    /**
     * 修改流水号信息模块
     *
     * @param baseSerialNumber 流水号信息模块
     * @return 结果
     */
    @Override
    public int updateBaseSerialNumber(BasSerialNumber baseSerialNumber) {
        return baseMapper.updateById(baseSerialNumber);
    }

    /**
     * 批量删除流水号信息模块
     *
     * @param ids 需要删除的流水号信息模块ID
     * @return 结果
     */
    @Override
    public int deleteBaseSerialNumberByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除流水号信息模块信息
     *
     * @param id 流水号信息模块ID
     * @return 结果
     */
    @Override
    public int deleteBaseSerialNumberById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public String generateNumber(String code) {
        List<String> list = this.generateNumbers(code, 1);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<String> generateNumbers(String code, int num) {
        // code不能为空
        if (StringUtils.isEmpty(code)) {
            return null;
        }
        // key
        String key = applicationName + ":serialnumber:" + code;
        RLock lock = redissonClient.getLock(key);
        // time 5 seconds
        long time = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        BasSerialNumber serialNumber = null;
        try {
            if (lock.tryLock(time, timeUnit)) {
                serialNumber = this.updateSerialNumber(code, num);
            }
        } catch (InterruptedException e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            lock.unlock();
        }
        return this.getSerialNumbers(serialNumber, num);
    }

    private BasSerialNumber updateSerialNumber(String code, int num) {
        // 查询流水号
        QueryWrapper<BasSerialNumber> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", code);
        BasSerialNumber serialNumber = super.getOne(queryWrapper);
        if (null == serialNumber) {
            throw new CommonException("999", "业务编码[" + code + "]未配置");
        }
        // 修改流水号相关信息
        BasSerialNumber updateSerialNumber = new BasSerialNumber();
        updateSerialNumber.setId(serialNumber.getId());
        if (serialNumber.getType() == SerialNumberTypeEnum.CYCLE.ordinal()) {
            Date currentDate = new Date();
            String currentCycle = DateFormatUtils.format(currentDate, serialNumber.getCycleFormat());
            if (!currentCycle.equals(serialNumber.getCurrentCycle())) {
                serialNumber.setCurrentSequence(0L);
                serialNumber.setCurrentCycle(currentCycle);
                // 更新当前序列号，当前周期
                updateSerialNumber.setCurrentSequence(0L);
                updateSerialNumber.setCurrentCycle(currentCycle);
            }
        }
        int step = serialNumber.getStep();
        long end = serialNumber.getCurrentSequence() + step * num;
        // 更新当前序列号
        updateSerialNumber.setCurrentSequence(end);
        // 更新
        super.updateById(updateSerialNumber);
        return serialNumber;
    }

    private List<String> getSerialNumbers(BasSerialNumber serialNumber, int num) {
        if (null == serialNumber) {
            return null;
        }
        String code = serialNumber.getCode();
        int step = serialNumber.getStep();
        // 处理流水号
        List<String> numberList = new ArrayList<>();
        // 缓存MessageFormat
        MessageFormat messageFormat = MSG_FORMAT_MAP.get(code);
        if (null == messageFormat) {
            messageFormat = new MessageFormat(serialNumber.getTemplate());
            MSG_FORMAT_MAP.put(code, messageFormat);
        }
        long start = serialNumber.getCurrentSequence();
        for (int i = 0; i < num; i++) {
            start += step;
            numberList.add(formatNumber(messageFormat, serialNumber, start));
        }
        return numberList;
    }

    /**
     * 格式化流水号
     *
     * @param messageFormat messageFormat
     * @param serialNumber  serialNumber
     * @param start         start
     * @return string
     */
    private String formatNumber(MessageFormat messageFormat, BasSerialNumber serialNumber, long start) {
        // 周期 + 增长值
        Object[] formatArgs;
        // 填充字符串
        String fullChar = "0";
        String fullBegin = fullBegin(String.valueOf(start), serialNumber.getLength(), fullChar);
        if (serialNumber.getType() == SerialNumberTypeEnum.CYCLE.ordinal()) {
            formatArgs = new String[2];
            formatArgs[0] = serialNumber.getCurrentCycle();
            formatArgs[1] = fullBegin;
        } else {
            formatArgs = new String[1];
            formatArgs[0] = fullBegin;
        }
        // 处理前缀
        String prefix = serialNumber.getPrefix();
        if (StringUtils.isEmpty(prefix)) {
            prefix = "";
        }
        return prefix + messageFormat.format(formatArgs);
    }

    /**
     * 字符串填充头部
     *
     * @param str 字符串
     * @return string
     */
    private String fullBegin(String str, int len, String fullChar) {
        int n = len - str.length();
        if (n > 0) {
            StringBuilder builder = new StringBuilder("");
            for (int i = 0; i < n; i++) {
                builder.append(fullChar);
            }
            builder.append(str);
            return builder.toString();
        }
        return str;
    }

}
