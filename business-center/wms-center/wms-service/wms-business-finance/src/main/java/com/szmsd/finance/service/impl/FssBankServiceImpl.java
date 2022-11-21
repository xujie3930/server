package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.domain.FssBank;
import com.szmsd.finance.mapper.FssBankMapper;
import com.szmsd.finance.service.FssBankService;
import com.szmsd.finance.vo.FssBankVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FssBankServiceImpl extends ServiceImpl<FssBankMapper, FssBank> implements FssBankService {


    @Override
    public R<List<FssBankVO>> findAll() {

        List<FssBank> fssBankVOS = baseMapper.selectList(Wrappers.<FssBank>query());

        List<FssBankVO> fssVO = this.generatorBank(fssBankVOS);

        return R.ok(fssVO);
    }

    @Override
    public R<List<FssBankVO>> findBank() {

        R<List<FssBankVO>> bankRs = this.findAll();

        if(bankRs.getCode() != 200){
            return R.failed(bankRs.getMsg());
        }

        List<FssBankVO> fssBankVOS = bankRs.getData();

        if(CollectionUtils.isEmpty(fssBankVOS)){
            return R.ok();
        }

        List<FssBankVO> studentList = new ArrayList<>(fssBankVOS.stream()
                .collect(Collectors.toMap(FssBankVO::getBankCode, Function.identity(), (oldValue, newValue) -> oldValue))
                .values());

        return R.ok(studentList);
    }

    @Override
    public R<List<FssBankVO>> findBankAccount(String bankCode,String currencyCode) {

        QueryWrapper<FssBank> queryWrapper = new QueryWrapper<>();

        if(StringUtils.isNotEmpty(bankCode)){
            queryWrapper.eq("bank_code",bankCode);
        }

        if(StringUtils.isNotEmpty(currencyCode)){
            queryWrapper.eq("currency_code",currencyCode);
        }

        List<FssBank> fssBankVOS = baseMapper.selectList(queryWrapper);

        if(CollectionUtils.isEmpty(fssBankVOS)){
            return R.ok();
        }

        List<FssBankVO> fssVO = this.generatorBank(fssBankVOS);

        return R.ok(fssVO);
    }

    private List<FssBankVO> generatorBank(List<FssBank> fssBankList) {

        List<FssBankVO> fssBankVOS = new ArrayList<>();

        for(FssBank fssBank : fssBankList){
            FssBankVO fssBankVO = new FssBankVO();
            fssBankVO.setBankAccount(fssBank.getBankAccount());
            fssBankVO.setBankCode(fssBank.getBankCode());
            fssBankVO.setBankName(fssBank.getBankName());
            fssBankVO.setBankId(fssBank.getId().toString());
            fssBankVO.setCurrencyCode(fssBank.getCurrencyCode());
            fssBankVOS.add(fssBankVO);
        }

        return fssBankVOS;
    }
}
