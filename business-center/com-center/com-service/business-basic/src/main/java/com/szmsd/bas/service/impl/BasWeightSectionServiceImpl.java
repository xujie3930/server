package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasWeightSectionDto;
import com.szmsd.bas.api.domain.BasWeightSectionQueryDto;
import com.szmsd.bas.api.domain.BasWeightSectionVo;
import com.szmsd.bas.dao.BasWeightSectionMapper;
import com.szmsd.bas.domain.BasWeightSection;
import com.szmsd.bas.service.IBasWeightSectionService;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.LogisticsExceptionUtil;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.szmsd.common.core.web.controller.BaseController.getLen;

/**
 * <p>
 * 重量区间设置 服务实现类
 * </p>
 *
 * @author 2
 * @since 2021-01-11
 */
@Service
@Slf4j
public class BasWeightSectionServiceImpl extends ServiceImpl<BasWeightSectionMapper, BasWeightSection> implements IBasWeightSectionService {


    /**
     * 查询重量区间设置模块列表
     *
     * @param queryDto 重量区间设置模块
     * @return 重量区间设置模块
     */
    @Override
    public List<BasWeightSectionVo> selectBasWeightSectionList(BasWeightSectionQueryDto queryDto) {
        QueryWrapper<BasWeightSection> where = new QueryWrapper<BasWeightSection>();
        if (StringUtils.isNotEmpty(queryDto.getUserCode())) {
            where.eq("user_code", queryDto.getUserCode());
        }
        where.orderByDesc("create_time");
        return BeanMapperUtil.mapList(baseMapper.selectList(where), BasWeightSectionVo.class);
    }

    /**
     * 新增重量区间设置模块
     *
     * @param dto 重量区间设置模块
     * @return 结果
     */
    @Override
    public int insertBasWeightSection(BasWeightSectionDto dto) {
        if (null == dto || StringUtils.isEmpty(dto.getUserCode()) || CollectionUtils.isEmpty(dto.getWeightDto())) {
            throw LogisticsExceptionUtil.getException(ExceptionMessageEnum.CANNOTBENULL, getLen());
        }
        int delete = baseMapper.delete(new QueryWrapper<BasWeightSection>().eq("user_code", dto.getUserCode()));
        log.info("刪除{}的重量段", delete);
        List<BasWeightSection> collect = dto.getWeightDto().stream().filter(e -> null != e.getWeightStart() || null != e.getWeightEnd())
                .map(e -> new BasWeightSection()
                        .setUserCode(dto.getUserCode())
                        .setUserName(dto.getUserName())
                        .setWeightStart(e.getWeightStart())
                        .setWeightEnd(e.getWeightEnd())
                ).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            log.info("无需新增的重量段");
            return 0;
        }
        boolean b = this.saveBatch(collect);
        if (!b) {
            log.info("新增重量段失败");
        }
        return b ? collect.size() : 0;
    }

    @Override
    public int deleteBasWeightSection(BasWeightSectionDto dto) {
        int res = 0;
        if (null != dto && CollectionUtils.isNotEmpty(dto.getUserCodes())) {
            res = baseMapper.delete(new QueryWrapper<BasWeightSection>().in("user_code", dto.getUserCodes()));
            log.info("删除重量段:{}", res);
        }
        return res;
    }
}

