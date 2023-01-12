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
import java.util.Calendar;
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
              //当前日期求出周期
//              Date date=new Date();
//              SimpleDateFormat dateFm = new SimpleDateFormat("EEEE");

              String currSun = weekDay();
              log.info("自动建揽收单数据周期：{}",currSun);
              //如果相同生成该周期的揽收单
             if (x.getWeekName().equals(currSun)){

                 PackageManagement packageManagement=new PackageManagement();
                 BeanUtils.copyProperties(x,packageManagement);
                 packageManagement.setId(null);
                 packageManagement.setExpectedDeliveryTime(LocalDate.now());
                 packageManagement.setCreateTime(new Date());
                 packageManagement.setCreateByName("系统自动创建");
                 iPackageMangServeService.insertPackageManagementjob(packageManagement);
             }
          });
      }

    }

    public String weekDay() {
        Date date = new Date();
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int i = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (i < 0) {
            i = 0;
        }
      return weekDays[i];
    }

}
