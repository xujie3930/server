package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.domain.BasPartner;
import com.szmsd.bas.mapper.BasPartnerMapper;
import com.szmsd.bas.service.IBasPartnerService;
import com.szmsd.common.core.exception.com.CommonException;
import org.springframework.stereotype.Service;

@Service
public class BasPartnerServiceImpl extends ServiceImpl<BasPartnerMapper, BasPartner> implements IBasPartnerService {

    @Override
    public boolean save(BasPartner entity) {
        this.exists(entity, true);
        return super.save(entity);
    }

    /**
     * 判断伙伴编码是否存在
     *
     * @param entity       实体
     * @param saveOrUpdate 新增true，修改false
     */
    private void exists(BasPartner entity, boolean saveOrUpdate) {
        LambdaQueryWrapper<BasPartner> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BasPartner::getPartnerCode, entity.getPartnerCode());
        if (!saveOrUpdate) {
            queryWrapper.ne(BasPartner::getId, entity.getId());
        }
        if (super.count(queryWrapper) > 0) {
            throw new CommonException("编码已存在：" + entity.getPartnerCode());
        }
    }

    @Override
    public boolean update(BasPartner entity) {
        this.exists(entity, false);
        return super.updateById(entity);
    }

    @Override
    public boolean delete(BasPartner entity) {
        return super.removeById(entity.getId());
    }

    @Override
    public BasPartner getByCode(BasPartner entity) {
        LambdaQueryWrapper<BasPartner> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BasPartner::getPartnerCode, entity.getPartnerCode());
        queryWrapper.last("LIMIT 1");
        return super.getOne(queryWrapper);
    }
}
