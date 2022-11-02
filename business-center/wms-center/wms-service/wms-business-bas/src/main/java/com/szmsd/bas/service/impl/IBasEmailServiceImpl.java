package com.szmsd.bas.service.impl;

import com.szmsd.bas.domain.BasEmail;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.bas.mapper.BasEmailMapper;
import com.szmsd.bas.service.IBasEmailService;
import com.szmsd.common.core.domain.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class IBasEmailServiceImpl implements IBasEmailService {
    @Autowired
    private BasEmailMapper basEmailMapper;

    @Override
    public R sendEmail(EmailDto emailDto) {
        try {

            List<EmailObjectDto> list=emailDto.getList();
            list.forEach(x->{
                BasEmail basEmail=new BasEmail();
                BeanUtils.copyProperties(x,basEmail);
                basEmail.setModularType(emailDto.getModularType());
                basEmail.setEmpTo(x.getEmail());
                basEmail.setEmpCode(emailDto.getEmpCode());
                basEmail.setCreateTime(new Date());
                basEmailMapper.insertSelective(basEmail);

            });


           return R.ok("成功");
        }catch (Exception e){
            e.printStackTrace();
            return R.failed("保存失败");
        }

    }
}
