package com.szmsd.pack.quartz;

import com.szmsd.pack.domain.PackageManagement;
import com.szmsd.pack.domain.PackageManagementConfig;
import com.szmsd.pack.dto.PackageMangAddDTO;
import com.szmsd.pack.mapper.PackageManagementConfigMapper;

import com.szmsd.pack.service.IPackageMangServeService;
import io.micrometer.core.instrument.binder.BaseUnits;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Slf4j
public class PackageMangJob extends QuartzJobBean {

    @Autowired
    private PackageManagementConfigMapper packageManagementConfigMapper;
    @Autowired
    private IPackageMangServeService iPackageMangServeService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //查询需要自动创建的模板
      List<PackageManagementConfig> packageManagementConfigList=packageManagementConfigMapper.selectPackageManagementConfigJob();
      if (packageManagementConfigList.size()>0){
          packageManagementConfigList.forEach(x->{
              log.info("自动建揽收单数据：{}",x);
              //当前日期求出周期
              Date date=new Date();
              SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");

              String currSun = dateFm.format(date);
              //如果相同生成该周期的揽收单
             if (x.getWeekName().equals(currSun)){
                 PackageManagement packageManagement=new PackageManagement();
                 BeanUtils.copyProperties(x,packageManagement);
                 packageManagement.setId(null);
                 packageManagement.setExpectedDeliveryTime(LocalDate.now());
                 packageManagement.setCreateTime(new Date());
                 packageManagement.setCreateByName("系统自动创建");
                 iPackageMangServeService.insertPackageManagementjob(packageManagement);
                 log.info("自动建揽收单数据成功");
             }
          });
      }
    }

}
