package com.szmsd.bas.api.service.impl;

import com.szmsd.bas.api.feign.SerialNumberFeignService;
import com.szmsd.bas.api.service.SerialNumberClientService;
import com.szmsd.bas.dto.GenerateNumberDto;
import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2020-12-02 002 9:42
 */
@Service
public class SerialNumberClientServiceImpl implements SerialNumberClientService {

    @Autowired
    private SerialNumberFeignService serialNumberFeignService;

    @Override
    public String generateNumber(String code) {
        GenerateNumberDto dto = new GenerateNumberDto();
        dto.setCode(code);
        R<String> r = this.serialNumberFeignService.generateNumber(dto);
        if (null == r || Constants.SUCCESS != r.getCode()) {
            throw new CommonException("999", "生成流水号失败");
        }
        return r.getData();
    }

    @Override
    public List<String> generateNumbers(String code, int num) {
        GenerateNumberDto dto = new GenerateNumberDto();
        dto.setCode(code);
        dto.setNum(num);
        R<List<String>> listR = this.serialNumberFeignService.generateNumbers(dto);
        if (null == listR || Constants.SUCCESS != listR.getCode()) {
            throw new CommonException("999", "生成流水号失败");
        }
        return listR.getData();
    }
}
