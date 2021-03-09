package com.szmsd.http.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.mapper.HtpRequestLogMapper;
import com.szmsd.http.service.IHtpRequestLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * http请求日志 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-09
 */
@Service
public class HtpRequestLogServiceImpl extends ServiceImpl<HtpRequestLogMapper, HtpRequestLog> implements IHtpRequestLogService {

    /**
     * 查询http请求日志模块
     *
     * @param id http请求日志模块ID
     * @return http请求日志模块
     */
    @Override
    public HtpRequestLog selectHtpRequestLogById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询http请求日志模块列表
     *
     * @param htpRequestLog http请求日志模块
     * @return http请求日志模块
     */
    @Override
    public List<HtpRequestLog> selectHtpRequestLogList(HtpRequestLog htpRequestLog) {
        QueryWrapper<HtpRequestLog> where = new QueryWrapper<HtpRequestLog>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增http请求日志模块
     *
     * @param htpRequestLog http请求日志模块
     * @return 结果
     */
    @Override
    public int insertHtpRequestLog(HtpRequestLog htpRequestLog) {
        return baseMapper.insert(htpRequestLog);
    }

    /**
     * 修改http请求日志模块
     *
     * @param htpRequestLog http请求日志模块
     * @return 结果
     */
    @Override
    public int updateHtpRequestLog(HtpRequestLog htpRequestLog) {
        return baseMapper.updateById(htpRequestLog);
    }

    /**
     * 批量删除http请求日志模块
     *
     * @param ids 需要删除的http请求日志模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpRequestLogByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除http请求日志模块信息
     *
     * @param id http请求日志模块ID
     * @return 结果
     */
    @Override
    public int deleteHtpRequestLogById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public int add(HtpRequestLog log) {
        log.setRequestHeader(this.substring(log.getRequestHeader(), 0xff));
        log.setResponseHeader(this.substring(log.getResponseHeader(), 0xff));
        return this.baseMapper.insert(log);
    }

    private String substring(String text, int maxLength) {
        return StringUtils.substring(text, 0, maxLength);
    }
}

