package com.szmsd.open.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import com.szmsd.open.domain.OpnRequestLog;
import com.szmsd.open.mapper.OpnRequestLogMapper;
import com.szmsd.open.service.IOpnRequestLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 请求日志 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-08
 */
@Service
public class OpnRequestLogServiceImpl extends ServiceImpl<OpnRequestLogMapper, OpnRequestLog> implements IOpnRequestLogService {


    /**
     * 查询请求日志模块
     *
     * @param id 请求日志模块ID
     * @return 请求日志模块
     */
    @Override
    public OpnRequestLog selectOpnRequestLogById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询请求日志模块列表
     *
     * @param opnRequestLog 请求日志模块
     * @return 请求日志模块
     */
    @Override
    public List<OpnRequestLog> selectOpnRequestLogList(OpnRequestLog opnRequestLog) {
        QueryWrapper<OpnRequestLog> queryWrapper = Wrappers.query();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "trace_id", opnRequestLog.getTraceId());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "request_uri", opnRequestLog.getRequestUri());
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.LIKE, "request_body", opnRequestLog.getRequestBody());
        if (null != opnRequestLog.getRequestTimeStart() && null != opnRequestLog.getRequestTimeEnd()) {
            queryWrapper.between("request_time", DateFormatUtils.format(opnRequestLog.getRequestTimeStart(), "yyyy-MM-dd"), DateFormatUtils.format(opnRequestLog.getRequestTimeEnd(), "yyyy-MM-dd"));
        }
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 新增请求日志模块
     *
     * @param opnRequestLog 请求日志模块
     * @return 结果
     */
    @Override
    public int insertOpnRequestLog(OpnRequestLog opnRequestLog) {
        return baseMapper.insert(opnRequestLog);
    }

    /**
     * 修改请求日志模块
     *
     * @param opnRequestLog 请求日志模块
     * @return 结果
     */
    @Override
    public int updateOpnRequestLog(OpnRequestLog opnRequestLog) {
        return baseMapper.updateById(opnRequestLog);
    }

    /**
     * 批量删除请求日志模块
     *
     * @param ids 需要删除的请求日志模块ID
     * @return 结果
     */
    @Override
    public int deleteOpnRequestLogByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除请求日志模块信息
     *
     * @param id 请求日志模块ID
     * @return 结果
     */
    @Override
    public int deleteOpnRequestLogById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int add(OpnRequestLog log) {
        log.setRequestHeader(this.substring(log.getRequestHeader(), 255));
        log.setRequestBody(this.substring(log.getRequestBody(), 40960));
        log.setResponseHeader(this.substring(log.getResponseHeader(), 255));
        log.setResponseBody(this.substring(log.getResponseBody(), 10240));
        return this.baseMapper.insert(log);
    }

    private String substring(String text, int maxLength) {
        return StringUtils.substring(text, 0, maxLength);
    }
}

