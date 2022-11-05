package com.szmsd.http.quartz;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.BasSellerEmailDto;
import com.szmsd.bas.dto.ServiceConditionDto;
import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.domain.BasCustomPricesgrade;
import com.szmsd.http.dto.custom.CustomPricesDiscountDto;
import com.szmsd.http.dto.custom.CustomPricesGradeDto;
import com.szmsd.http.dto.custom.CustomPricesPageDto;
import com.szmsd.http.mapper.BasCustomPricesgradeMapper;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CustomPricesgradeJob extends QuartzJobBean {
         @Autowired
         private BasCustomPricesgradeMapper basCustomPricesgradeMapper;
         @Autowired
         private HtpCustomPricesFeignService htpCustomPricesFeignService;
         @Autowired
         private BasSellerFeignService basSellerFeignService;
         @Override
        protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
             R<List<BasSellerEmailDto>> r= basSellerFeignService.queryAllSellerCodeAndEmail();
             List<BasSellerEmailDto> basSellerEmailDtoList=r.getData();
             List<String> sellerCodeList=basSellerEmailDtoList.stream().map(BasSellerEmailDto::getSellerCode).collect(Collectors.toList());
             sellerCodeList.forEach(s->{
                 R<CustomPricesPageDto> r1 = htpCustomPricesFeignService.page(s);
                 CustomPricesPageDto customPricesPageDto=r1.getData();
                 List<CustomPricesGradeDto> customPricesGradeDtoList=customPricesPageDto.getCustomGradeTemplates();

                 if (customPricesGradeDtoList.size()>0){
                     customPricesGradeDtoList.forEach(i->{

                         BasCustomPricesgrade basCustomPricesgrade=new BasCustomPricesgrade();
                         BeanUtils.copyProperties(i,basCustomPricesgrade);
                         basCustomPricesgrade.setId(null);
                         basCustomPricesgrade.setCustomprType("1");
                         basCustomPricesgrade.setCreateByName("系统自动创建");
                         basCustomPricesgrade.setShowTime(new Date());
                         basCustomPricesgrade.setOrderOn(i.getOrder());
                         basCustomPricesgrade.setSellerCode(s);
                         List<BasCustomPricesgrade>   basCustomPricesgrade1 =basCustomPricesgradeMapper.selectByPrimaryKey(basCustomPricesgrade);
                         if (basCustomPricesgrade1.size()>0){

                             basCustomPricesgradeMapper.updateByPrimaryKeySelectiveby(basCustomPricesgrade);
                         }

                         //BasCustomPricesgrade   basCustomPricesgrade2 =basCustomPricesgradeMapper.selectByPrimaryKeys(i.getTemplateId(),"1");
                         if (basCustomPricesgrade1.size()==0){
                             basCustomPricesgradeMapper.insertSelective(basCustomPricesgrade);
                         }


                     });


                 }
                 List<CustomPricesDiscountDto> customPricesDiscountDtoList=customPricesPageDto.getCustomDiscountTemplates();

                 if (customPricesDiscountDtoList.size()>0){
                     customPricesDiscountDtoList.forEach(q->{
                         BasCustomPricesgrade basCustomPricesgrade=new BasCustomPricesgrade();
                         BeanUtils.copyProperties(q,basCustomPricesgrade);
                         basCustomPricesgrade.setId(null);
                         basCustomPricesgrade.setCustomprType("0");
                         basCustomPricesgrade.setOrderOn(q.getOrder());
                         basCustomPricesgrade.setCreateByName("系统自动创建");
                         basCustomPricesgrade.setShowTime(new Date());
                         basCustomPricesgrade.setSellerCode(s);
                         List<BasCustomPricesgrade>   basCustomPricesgrade1 =basCustomPricesgradeMapper.selectByPrimaryKey(basCustomPricesgrade);
                         if (basCustomPricesgrade1.size()>0){
                             basCustomPricesgradeMapper.updateByPrimaryKeySelectiveby(basCustomPricesgrade);
                         }

//                         BasCustomPricesgrade   basCustomPricesgrade2 =basCustomPricesgradeMapper.selectByPrimaryKeys(q.getTemplateId(),"0");
                         if (basCustomPricesgrade1.size()==0){
                             basCustomPricesgradeMapper.insertSelective(basCustomPricesgrade);
                         }
                     });


                 }

             });

        }
}
