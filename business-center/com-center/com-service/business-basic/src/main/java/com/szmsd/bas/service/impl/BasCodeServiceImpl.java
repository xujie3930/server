package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.dao.BasCodeMapper;
import com.szmsd.bas.domain.BasCode;
import com.szmsd.bas.domain.BasCodeDto;
import com.szmsd.bas.service.BasCodeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-29 9:35
 * @Description
 */
@Service
@Slf4j
public class BasCodeServiceImpl implements BasCodeService {

    @Resource
    private BasCodeMapper basCodeMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public R createCode(BasCodeDto basCodeDto) {
        if (StringUtils.isBlank(basCodeDto.getAppId())) {
            return R.failed("appId不能为空!");
        }
        if (StringUtils.isBlank(basCodeDto.getCode())) {
            return R.failed("code不能为空!");
        }
        basCodeDto.setStatus(1);
        List<BasCode> basCodes = this.basCodeMapper.list(basCodeDto);
        if (CollectionUtils.isEmpty(basCodes)) {
            return R.failed("请先维护编码生成规则!");
        }
        if (basCodes.size() > 1) {
            return R.failed("查询到多条编码生成规则!");
        }
        //获取锁
        BasCode basCode = this.basCodeMapper.getLock(basCodes.get(0).getId());
        //增量步长
        int increment = basCode.getIncrementBy() == null ? 1 : basCode.getIncrementBy();
        //最新单号
        long lastNumber = basCode.getLastNumber() == null ? (basCode.getMinValue() == null ? 0 : basCode.getMinValue() - increment) : basCode.getLastNumber();
        //时间格式
        String datePatter = "";
        if (null != basCode.getIsTimeStamp() &&
                1 == basCode.getIsTimeStamp() && StringUtils.isNotEmpty(basCode.getPatter()) && checkPattern(basCode.getPatter())) {
            //使用时间格式作为生成编码 前缀 + 时间格式 + 数字 + 后缀 ,倘若修改patter ，多次重复不保证生成唯一编码
            try {
                String str = DateUtils.parseDateToStr(basCode.getPatter().trim(), new Date());
                //最新的时间
                if (!str.trim().equalsIgnoreCase(basCode.getLastPatter())) {
                    //需要更新为当前时间
                    lastNumber = basCode.getMinValue() == null ? 0 : basCode.getMinValue() - increment;
                    datePatter = str;
                } else {
                    datePatter = basCode.getLastPatter();
                }
            } catch (Exception e) {
                log.info("日期编码格式失败:{}", e.toString());
                throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPBASIS006, getLen(), basCodeDto.getCode());
            }
        }
        //生成数量
        int count = basCodeDto.getCount() < 1 ? 1 : basCodeDto.getCount();
        List<Long> list = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            list.add(lastNumber + i * increment);
        }
        //判断最大值
        long max = list.get(count - 1);
        if (max > basCode.getMaxValue()) {
            return R.failed("已超出最大值范围，请重新维护!");
        }
        //修改最大值
        basCode.setLastNumber(max);
        basCode.setLastPatter(datePatter);
        this.basCodeMapper.update(basCode);

        String prefix = StringUtils.isBlank(basCode.getPrefix()) ? "" : basCode.getPrefix();
        String suffix = StringUtils.isBlank(basCode.getSuffix()) ? "" : basCode.getSuffix();
        String finalDatePatter = datePatter;
        List<String> result = list.stream().map(num -> prefix + finalDatePatter + fill(num, basCode.getLength()) + suffix).collect(Collectors.toList());
        return R.ok(result);
    }

    /**
     * 补位
     *
     * @param num
     * @param length
     * @return
     */
    private String fill(long num, int length) {
        String res = String.valueOf(num);
        int a = length - res.length();
        if (a > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < a; i++) {
                sb.append("0");
            }
            sb.append(res);
            return sb.toString();
        }
        return res;
    }

    public int saveBasCode(BasCode basCode) {
        if (null == basCode || StringUtils.isEmpty(basCode.getCode())) {
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPORDER026, getLen());
        }
        if (StringUtils.isEmpty(basCode.getAppId())) {
            basCode.setAppId("gfs");
        }
        List<BasCode> basCodes = basCodeMapper.selectList(new QueryWrapper<BasCode>().eq("app_id", basCode.getAppId())
                .eq("code", basCode.getCode()));
        if (!CollectionUtils.isEmpty(basCodes) && basCodes.size() > 0) {
            log.info("该code已存在", basCode.getCode());
            //更新
            BasCode oldBasCode = basCodes.get(0);
            if (null == oldBasCode.getStatus() || 0 == oldBasCode.getStatus()) {
                basCode.setStatus(1); //重新启用
            }
            if (StringUtils.isNotEmpty(basCode.getSequenceName())) {
                basCode.setSequenceName(basCode.getSequenceName() + " - 类型");
            }
            return basCodeMapper.update(basCode.setId(oldBasCode.getId()));
        } else {
            //新增
            if (null == basCode.getLength()) {
                basCode.setLength(10);
            }
            if (null == basCode.getMinValue()) {
                basCode.setMinValue(1);
            }
            if (null == basCode.getMaxValue()) {
                basCode.setMaxValue(9999999999L);
            }
            if (null == basCode.getIncrementBy()) {
                basCode.setIncrementBy(1);
            }
            if (null == basCode.getStatus()) {
                basCode.setStatus(1);
            }
            if (StringUtils.isNotEmpty(basCode.getSequenceName())) {
                basCode.setSequenceName(basCode.getSequenceName() + " - 类型");
            }
            return basCodeMapper.save(basCode
                    .setId(UUID.randomUUID().toString().replaceAll("-", ""))
            );
        }
    }

    public int deleteBasCode(List<BasCode> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.EXPORDER026, getLen());
        }
        ListUtils.emptyIfNull(list).forEach(e -> {
            if (StringUtils.isEmpty(e.getAppId())) {
                e.setAppId("gfs");
            }
            BasCode basCodeOld = basCodeMapper.selectOne(new QueryWrapper<BasCode>().eq("app_id", e.getAppId())
                    .eq("code", e.getCode()));
            if (null != basCodeOld) {
                basCodeMapper.update(new BasCode().setId(basCodeOld.getId()).setStatus(0));
            }
        });
        return 1;
    }

    private boolean checkPattern(String pattern) {
        String[] parsePatterns = {
                "yyyy-MM-dd", "yyyy-MM",
                "yyyy/MM/dd", "yyyy/MM",
                "yyyy.MM.dd", "yyyy.MM",
                "yyyy", "yyyyMMdd", "yyyyMM", "MMdd", "MM", "dd"
        };

        List<String> strings = Arrays.asList(parsePatterns);
        return strings.contains(pattern);
    }
}
