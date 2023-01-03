package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.ChargeRelation;
import com.szmsd.finance.mapper.ChargeRelationMapper;
import com.szmsd.finance.service.ChargeRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargeRelationServiceImpl extends ServiceImpl<ChargeRelationMapper, ChargeRelation> implements ChargeRelationService {

    @Override
    public R<List<ChargeRelation>> selectNature() {

        List<ChargeRelation> chargeRelations = baseMapper.selectList(Wrappers.<ChargeRelation>query().lambda());

        if(CollectionUtils.isEmpty(chargeRelations)){
            return R.ok();
        }

        List<ChargeRelation> unique = chargeRelations.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ChargeRelation::getNature))), ArrayList::new)
        );

        return R.ok(unique);
    }

    @Override
    public R<List<ChargeRelation>> selectCategory(String nature) {

        List<ChargeRelation> chargeRelations = baseMapper.selectList(Wrappers.<ChargeRelation>query().lambda()
                .eq(ChargeRelation::getNature,nature)
        );

        return R.ok(chargeRelations);
    }

    @Override
    public R<List<ChargeRelation>> selectCharge(String nature, String category) {

        List<ChargeRelation> chargeRelations = baseMapper.selectList(Wrappers.<ChargeRelation>query().lambda()
                .eq(ChargeRelation::getNature,nature)
                .eq(ChargeRelation::getChargeCategoryChange,category)
        );

        return R.ok(chargeRelations);
    }
}
