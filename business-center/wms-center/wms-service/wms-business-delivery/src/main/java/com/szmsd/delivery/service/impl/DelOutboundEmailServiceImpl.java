package com.szmsd.delivery.service.impl;

import com.szmsd.bas.api.domain.BasEmployees;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBatchUpdateTrackingNoDto;
import com.szmsd.delivery.dto.DelOutboundBatchUpdateTrackingNoEmailDto;
import com.szmsd.delivery.dto.DelOutbounderrorEmail;
import com.szmsd.delivery.dto.DelOutboundsuccessEmail;
import com.szmsd.delivery.mapper.DelOutboundMapper;
import com.szmsd.delivery.service.DelOutboundEmailService;
import com.szmsd.http.domain.HtpRequestLog;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DelOutboundEmailServiceImpl implements DelOutboundEmailService {

    private Logger logger = LoggerFactory.getLogger(DelOutboundServiceImpl.class);
    @Autowired
    private DelOutboundMapper delOutboundMapper;

    @Autowired
    private BasFeignService basFeignService;
    @Override
    public R selectOmsWmsLog() {

        Date createTime=getStartTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DelOutbound> errorDekOuList=delOutboundMapper.selectOmsWmsLogerror(simpleDateFormat.format(createTime));
        List<DelOutbound> successDekOuList=delOutboundMapper.selectOmsWmsLogsuccess(simpleDateFormat.format(createTime));
        emailBatchUpdateTrackingNo(errorDekOuList,successDekOuList);
        return null;
    }


    /**
     * 发送邮箱
     * @param
     */

    public void emailBatchUpdateTrackingNo (List<DelOutbound> errorDekOuList,List<DelOutbound> successDekOuList) {
        //拿到成功的单号
        logger.info("发送邮箱进来-----");
        //失败数据组装
        Map<String,List<DelOutbounderrorEmail>> delOutbounderrorEmailMap =new HashMap<>();

        //成功数据组装
        Map<String,List<DelOutboundsuccessEmail>> delOutboundsuccessEmailMap =new HashMap<>();
        if (successDekOuList.size()>0){
            delOutboundsuccessEmailMap=delOutboundsuccessEmailMap(successDekOuList);
        }






        if (errorDekOuList.size()>0) {

            //查询用户，客户关系表
            List<BasSeller> basSellerList= delOutboundMapper.selectdelsellerCodes();
            List<DelOutbounderrorEmail> delOutbounderrorEmailList=new ArrayList<>();

            for (DelOutbound dto:errorDekOuList) {

                basSellerList.stream().filter(x -> x.getSellerCode().equals(dto.getCustomCode())).findFirst().ifPresent(basSeller -> {

                    DelOutbounderrorEmail delOutbounderrorEmail = new DelOutbounderrorEmail();
                    BeanUtils.copyProperties(dto, delOutbounderrorEmail);


                    if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                        if (!basSeller.getServiceManagerName().equals(basSeller.getServiceStaffName())) {




//                            delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceManagerName());
//                            delOutboundBatchUpdateTrackingNoEmailDto.setServiceManagerName(basSeller.getServiceManagerName());
                            if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {
                                delOutbounderrorEmail.setServiceStaffName(basSeller.getServiceStaffName());
                            }

                        }
                    }
                    if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {

//                        delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceStaffName());
//                        delOutboundBatchUpdateTrackingNoEmailDto.setServiceStaffName(basSeller.getServiceStaffName());

                        if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                            delOutbounderrorEmail.setServiceManagerName(basSeller.getServiceManagerName());
                        }
//                        delOutboundBatchUpdateTrackingNoEmailDtoList.add(delOutboundBatchUpdateTrackingNoEmailDto);

                    }


                    delOutbounderrorEmailList.add(delOutbounderrorEmail);

                });

            }

            BasEmployees basEmployees=new BasEmployees();
            //查询员工表
            R<List<BasEmployees>> basEmployeesR= basFeignService.empList(basEmployees);

            List<BasEmployees> basEmployeesList=basEmployeesR.getData();
            //找到员工中的邮箱
            for (DelOutbounderrorEmail dto:delOutbounderrorEmailList){
                List<String> serviceManagerStaffName=new ArrayList<>();
                basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceManagerName())).findFirst().ifPresent(i -> {
                    if (i.getEmail()!=null&&!i.getEmail().equals("")){
                        serviceManagerStaffName.add(i.getEmail());
                    }
                    dto.setServiceManagerName(i.getEmpName());
                });
                basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceStaffName())).findFirst().ifPresent(i -> {
                    if (i.getEmail()!=null&&!i.getEmail().equals("")){
                        serviceManagerStaffName.add(i.getEmail());
                    }


                    dto.setServiceStaffName(i.getEmpName());

                });
                //屏蔽添加客户邮箱
                //serviceManagerStaffName.add(dto.getSellerEmail());
                //去除重复邮箱
                List<String> listWithoutDuplicates = serviceManagerStaffName.stream().distinct().collect(Collectors.toList());

                String email = StringUtils.join(listWithoutDuplicates,",");
                dto.setEmail(email);


            }

            //将组合的数据 分解成Map<List> (邮箱为key,组合这个员工下的所有信息)
            delOutbounderrorEmailMap=delOutbounderrorEmailList.stream().collect(Collectors.groupingBy(DelOutbounderrorEmail::getCustomCode));
//            //循环map，得到每一组的数据 然后生成excel需要数据
//            for (Map.Entry<String, List<DelOutbounderrorEmail>> entry : DelOutbounderrorEmailMap.entrySet()) {
//                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
//                logger.info("组合参数：{}",entry.getValue());
//                ExcleDelOutboundBatchUpdateTracking(entry.getValue(),entry.getKey(), filepath);
//            }

        }


            Set<String> delOutboundEmailsCode= new HashSet<>();
           delOutboundEmailsCode.addAll(delOutbounderrorEmailMap.keySet());
           delOutboundEmailsCode.addAll(delOutboundsuccessEmailMap.keySet());

        System.out.println(delOutboundEmailsCode);




    }

    private Map<String, List<DelOutboundsuccessEmail>> delOutboundsuccessEmailMap(List<DelOutbound> successDekOuList) {
        //查询用户，客户关系表
        List<BasSeller> basSellerList= delOutboundMapper.selectdelsellerCodes();
        List<DelOutboundsuccessEmail> delOutboundsuccessEmailList=new ArrayList<>();

        for (DelOutbound dto:successDekOuList) {

            basSellerList.stream().filter(x -> x.getSellerCode().equals(dto.getCustomCode())).findFirst().ifPresent(basSeller -> {

                DelOutboundsuccessEmail delOutboundsuccessEmail = new DelOutboundsuccessEmail();
                BeanUtils.copyProperties(dto, delOutboundsuccessEmail);


                if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                    if (!basSeller.getServiceManagerName().equals(basSeller.getServiceStaffName())) {




//                            delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceManagerName());
//                            delOutboundBatchUpdateTrackingNoEmailDto.setServiceManagerName(basSeller.getServiceManagerName());
                        if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {
                            delOutboundsuccessEmail.setServiceStaffName(basSeller.getServiceStaffName());
                        }

                    }
                }
                if (basSeller.getServiceStaffName() != null && !basSeller.getServiceStaffName().equals("")) {

//                        delOutboundBatchUpdateTrackingNoEmailDto.setEmpCode(basSeller.getServiceStaffName());
//                        delOutboundBatchUpdateTrackingNoEmailDto.setServiceStaffName(basSeller.getServiceStaffName());

                    if (basSeller.getServiceManagerName() != null && !basSeller.getServiceManagerName().equals("")) {
                        delOutboundsuccessEmail.setServiceManagerName(basSeller.getServiceManagerName());
                    }
//                        delOutboundBatchUpdateTrackingNoEmailDtoList.add(delOutboundBatchUpdateTrackingNoEmailDto);

                }


                delOutboundsuccessEmailList.add(delOutboundsuccessEmail);

            });

        }

        BasEmployees basEmployees=new BasEmployees();
        //查询员工表
        R<List<BasEmployees>> basEmployeesR= basFeignService.empList(basEmployees);

        List<BasEmployees> basEmployeesList=basEmployeesR.getData();
        //找到员工中的邮箱
        for (DelOutboundsuccessEmail dto:delOutboundsuccessEmailList){
            List<String> serviceManagerStaffName=new ArrayList<>();
            basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceManagerName())).findFirst().ifPresent(i -> {
                if (i.getEmail()!=null&&!i.getEmail().equals("")){
                    serviceManagerStaffName.add(i.getEmail());
                }
                dto.setServiceManagerName(i.getEmpName());
            });
            basEmployeesList.stream().filter(x->x.getEmpCode().equals(dto.getServiceStaffName())).findFirst().ifPresent(i -> {
                if (i.getEmail()!=null&&!i.getEmail().equals("")){
                    serviceManagerStaffName.add(i.getEmail());
                }


                dto.setServiceStaffName(i.getEmpName());

            });
            //屏蔽添加客户邮箱
            //serviceManagerStaffName.add(dto.getSellerEmail());
            //去除重复邮箱
            List<String> listWithoutDuplicates = serviceManagerStaffName.stream().distinct().collect(Collectors.toList());

            String email = StringUtils.join(listWithoutDuplicates,",");
            dto.setEmail(email);


        }

        //将组合的数据 分解成Map<List> (邮箱为key,组合这个员工下的所有信息)
         return  delOutboundsuccessEmailList.stream().collect(Collectors.groupingBy(DelOutboundsuccessEmail::getCustomCode));

    }


    public static Date getStartTime() {
        Calendar time = Calendar.getInstance();

        time.add(Calendar.DATE, -1);

        time.set(Calendar.HOUR_OF_DAY, 18);

        time.set(Calendar.MINUTE, 0);

        time.set(Calendar.SECOND, 0);

        time.set(Calendar.MILLISECOND, 0);

        System.out.println(time.getTime());
       return time.getTime();
    }

    //前一天开始时间
//    public static Long getBeginDayOfYesterday() {
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        cal.add(Calendar.DAY_OF_MONTH, -1);
//        return cal.getTimeInMillis();
//    }



    //前一天结束时间
//    public static Long getEndDayOfYesterDay() {
//
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.HOUR_OF_DAY, 23);
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.add(Calendar.DAY_OF_MONTH, -1);
//
//
//        return cal.getTimeInMillis();
//    }

}
