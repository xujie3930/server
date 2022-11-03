package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.finance.domain.FssBank;
import com.szmsd.finance.mapper.FssBankMapper;
import com.szmsd.finance.service.FssBankService;
import com.szmsd.finance.vo.FssBankVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FssBankServiceImpl extends ServiceImpl<FssBankMapper, FssBank> implements FssBankService {


    @Override
    public R<List<FssBankVO>> findAll() {

        List<FssBank> fssBankVOS = baseMapper.selectList(Wrappers.<FssBank>query());

        List<FssBankVO> fssVO = this.generatorBank(fssBankVOS);

        return R.ok(fssVO);
    }

    private List<FssBankVO> generatorBank(List<FssBank> fssBankList) {

        List<FssBankVO> fssBankVOS = new ArrayList<>();

        for(FssBank fssBank : fssBankList){
            FssBankVO fssBankVO = new FssBankVO();
            fssBankVO.setBankAccount(fssBankVO.getBankAccount());
            fssBankVO.setBankCode(fssBank.getBankCode());
            fssBankVO.setBankName(fssBank.getBankName());
            fssBankVO.setBankId(fssBank.getId().toString());
            fssBankVOS.add(fssBankVO);
        }

        return fssBankVOS;
    }
}
