package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.command.ChargeReadExcelCmd;
import com.szmsd.delivery.domain.ChargeImport;
import com.szmsd.delivery.mapper.ChargeImportMapper;
import com.szmsd.delivery.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
public class ChargeServiceImpl extends ServiceImpl<ChargeImportMapper, ChargeImport> implements ChargeService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public R importExcel(MultipartFile file) {

        List<ChargeImport> chargeImportList = new ChargeReadExcelCmd(file).execute();
        boolean save = super.saveBatch(chargeImportList);
        if(save){
            return R.ok();
        }
        return R.failed("导入失败");
    }
}
