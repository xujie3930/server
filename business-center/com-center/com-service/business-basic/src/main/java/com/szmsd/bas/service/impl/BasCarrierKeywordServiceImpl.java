package com.szmsd.bas.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.dao.BasCarrierKeywordMapper;
import com.szmsd.bas.domain.BasCarrierKeyword;
import com.szmsd.bas.keyword.KeywordsInit;
import com.szmsd.bas.keyword.KeywordsUtil;
import com.szmsd.bas.service.IBasCarrierKeywordService;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author YM
 * @since 2022-01-24
 */
@Service
public class BasCarrierKeywordServiceImpl extends ServiceImpl<BasCarrierKeywordMapper, BasCarrierKeyword> implements IBasCarrierKeywordService {

    @Autowired
    private KeywordsInit keywordsInit;

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    @Override
    public BasCarrierKeyword selectBasCarrierKeywordById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询模块列表
     *
     * @param basCarrierKeyword 模块
     * @return 模块
     */
    @Override
    public List<BasCarrierKeyword> selectBasCarrierKeywordList(BasCarrierKeyword basCarrierKeyword) {
        LambdaQueryWrapper<BasCarrierKeyword> where = new LambdaQueryWrapper<BasCarrierKeyword>()
                .eq(StringUtils.isNotEmpty(basCarrierKeyword.getCarrierCode()), BasCarrierKeyword::getCarrierCode, basCarrierKeyword.getCarrierCode())
                .orderByDesc(BasCarrierKeyword::getId);
        return baseMapper.selectList(where);
    }

    /**
     * 新增模块
     *
     * @param basCarrierKeyword 模块
     * @return 结果
     */
    @Override
    public int insertBasCarrierKeyword(BasCarrierKeyword basCarrierKeyword) {
        return baseMapper.insert(basCarrierKeyword);
    }

    /**
     * 修改模块
     *
     * @param basCarrierKeyword 模块
     * @return 结果
     */
    @Override
    public int updateBasCarrierKeyword(BasCarrierKeyword basCarrierKeyword) {
        return baseMapper.updateById(basCarrierKeyword);
    }

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    @Override
    public int deleteBasCarrierKeywordByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public Boolean checkExistKeyword(String carrierCode, String text) {
        Map keywordsMap = keywordsInit.initKeyWord(carrierCode);
        KeywordsUtil.keywordsMap = keywordsMap;
        return KeywordsUtil.isContaintKeywords(text);
    }
}

