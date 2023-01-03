package com.szmsd.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.ChargeRelation;

import java.util.List;

public interface ChargeRelationService extends IService<ChargeRelation> {

    /**
     * 查询性质
     * @return
     */
    R<List<ChargeRelation>> selectNature();

    /**
     * 查询类型
     * @param nature
     * @return
     */
    R<List<ChargeRelation>> selectCategory(String nature);

    /**
     * 费用类型
     * @param nature
     * @return
     */
    R<List<ChargeRelation>> selectCharge(String nature,String category);
}
