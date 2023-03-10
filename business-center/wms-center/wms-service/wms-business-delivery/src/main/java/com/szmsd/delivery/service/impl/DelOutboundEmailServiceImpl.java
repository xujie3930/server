package com.szmsd.delivery.service.impl;

import com.szmsd.bas.api.domain.BasEmployees;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.bas.api.feign.EmailFeingService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.EmailDelOutboundError;
import com.szmsd.bas.dto.EmailDelOutboundSuccess;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.dto.EmailObjectDto;
import com.szmsd.chargerules.enums.DelOutboundOrderEnum;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
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

    @Autowired
    private EmailFeingService emailFeingService;
    @Override
    public R selectOmsWmsLog() {

        Date createTime=getStartTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DelOutbound> errorDekOuList=delOutboundMapper.selectOmsWmsLogerror(simpleDateFormat.format(createTime));
        List<DelOutbound> successDekOuList=delOutboundMapper.selectOmsWmsLogsuccess(simpleDateFormat.format(createTime));
        emailBatchUpdateTrackingNo(errorDekOuList,successDekOuList);

        //???????????????
        List<DelOutbounderrorEmail> delOutbounderrorEmailListsTwo= BeanMapperUtil.mapList(errorDekOuList, DelOutbounderrorEmail.class);
        List<DelOutboundsuccessEmail>delOutboundsuccessListsTwo= new ArrayList<>(); BeanMapperUtil.mapList(successDekOuList, DelOutboundsuccessEmail.class);
        successDekOuList.forEach(dto->{

            DelOutboundsuccessEmail delOutboundsuccessEmail = new DelOutboundsuccessEmail();
            BeanUtils.copyProperties(dto, delOutboundsuccessEmail);

            delOutboundsuccessEmail.setOrderType(DelOutboundOrderEnum.getName(dto.getOrderType()));
            delOutboundsuccessEmail.setCreateTime(simpleDateFormat.format(dto.getCreateTime()));
            delOutboundsuccessListsTwo.add(delOutboundsuccessEmail);

        });

        ExcleDelOutboundBatchUpdateTrackingTwo(delOutbounderrorEmailListsTwo,delOutboundsuccessListsTwo);

        return null;
    }


    /**
     * ????????????
     * @param
     */

    public void emailBatchUpdateTrackingNo (List<DelOutbound> errorDekOuList,List<DelOutbound> successDekOuList) {
        //?????????????????????
        logger.info("??????????????????-----");
        //??????????????????
        Map<String,List<DelOutbounderrorEmail>> delOutbounderrorEmailMap =new HashMap<>();

        //??????????????????
        Map<String,List<DelOutboundsuccessEmail>> delOutboundsuccessEmailMap =new HashMap<>();
        if (successDekOuList.size()>0){
            delOutboundsuccessEmailMap=delOutboundsuccessEmailMap(successDekOuList);
        }






        if (errorDekOuList.size()>0) {

            //??????????????????????????????
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
            //???????????????
            R<List<BasEmployees>> basEmployeesR= basFeignService.empList(basEmployees);

            List<BasEmployees> basEmployeesList=basEmployeesR.getData();
            //????????????????????????
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
                //????????????????????????
                //serviceManagerStaffName.add(dto.getSellerEmail());
                //??????????????????
                List<String> listWithoutDuplicates = serviceManagerStaffName.stream().distinct().collect(Collectors.toList());

                String email = StringUtils.join(listWithoutDuplicates,",");
                dto.setEmail(email);


            }

            //?????????????????? ?????????Map<List> (?????????key,????????????????????????????????????)
            delOutbounderrorEmailMap=delOutbounderrorEmailList.stream().collect(Collectors.groupingBy(DelOutbounderrorEmail::getCustomCode));
//            //??????map??????????????????????????? ????????????excel????????????
//            for (Map.Entry<String, List<DelOutbounderrorEmail>> entry : DelOutbounderrorEmailMap.entrySet()) {
//                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
//                logger.info("???????????????{}",entry.getValue());
//                ExcleDelOutboundBatchUpdateTracking(entry.getValue(),entry.getKey(), filepath);
//            }

        }


            Set<String> delOutboundEmailsCode= new HashSet<>();
           delOutboundEmailsCode.addAll(delOutbounderrorEmailMap.keySet());
           delOutboundEmailsCode.addAll(delOutboundsuccessEmailMap.keySet());

           for (String customCode:delOutboundEmailsCode){
               String email=null;
                List<DelOutbounderrorEmail> delOutbounderrorEmailLists=delOutbounderrorEmailMap.get(customCode);
               List<DelOutboundsuccessEmail> delOutboundsuccessEmailLists=delOutboundsuccessEmailMap.get(customCode);


               if (delOutbounderrorEmailLists!=null&&delOutbounderrorEmailLists.size()>0) {
                   email=delOutbounderrorEmailLists.get(0).getEmail();
               }
               if (delOutboundsuccessEmailLists!=null&&delOutboundsuccessEmailLists.size()>0){
                   email=delOutboundsuccessEmailLists.get(0).getEmail();
               }
               if (email!=null&&!email.equals("")){
                   //????????????
                   ExcleDelOutboundBatchUpdateTracking(delOutbounderrorEmailLists,delOutboundsuccessEmailLists,email,customCode);
               }


           }



    }


    private Map<String, List<DelOutboundsuccessEmail>> delOutboundsuccessEmailMap(List<DelOutbound> successDekOuList) {
        //??????????????????????????????
        List<BasSeller> basSellerList= delOutboundMapper.selectdelsellerCodes();
        List<DelOutboundsuccessEmail> delOutboundsuccessEmailList=new ArrayList<>();

        for (DelOutbound dto:successDekOuList) {

            basSellerList.stream().filter(x -> x.getSellerCode().equals(dto.getCustomCode())).findFirst().ifPresent(basSeller -> {

                DelOutboundsuccessEmail delOutboundsuccessEmail = new DelOutboundsuccessEmail();
                 SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                BeanUtils.copyProperties(dto, delOutboundsuccessEmail);
                delOutboundsuccessEmail.setOrderType(DelOutboundOrderEnum.getName(delOutboundsuccessEmail.getOrderType()));
               delOutboundsuccessEmail.setCreateTime(simpleDateFormat.format(dto.getCreateTime()));

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
        //???????????????
        R<List<BasEmployees>> basEmployeesR= basFeignService.empList(basEmployees);

        List<BasEmployees> basEmployeesList=basEmployeesR.getData();
        //????????????????????????
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
            //????????????????????????
            //serviceManagerStaffName.add(dto.getSellerEmail());
            //??????????????????
            List<String> listWithoutDuplicates = serviceManagerStaffName.stream().distinct().collect(Collectors.toList());

            String email = StringUtils.join(listWithoutDuplicates,",");
            dto.setEmail(email);


        }

        //?????????????????? ?????????Map<List> (?????????key,????????????????????????????????????)
         return  delOutboundsuccessEmailList.stream().collect(Collectors.groupingBy(DelOutboundsuccessEmail::getCustomCode));

    }


    public void ExcleDelOutboundBatchUpdateTracking( List<DelOutbounderrorEmail> delOutbounderrorEmailLists,List<DelOutboundsuccessEmail> delOutboundsuccessEmailLists,String email,String customCode){
        logger.info("??????????????????delOutbounderrorEmailLists???{}",delOutbounderrorEmailLists);
        logger.info("??????????????????delOutboundsuccessEmailLists???{}",delOutboundsuccessEmailLists);
        logger.info("????????????email???{}",email);
        if (delOutbounderrorEmailLists==null){
            delOutbounderrorEmailLists=new ArrayList<>();
        }
        if (delOutboundsuccessEmailLists==null){
            delOutboundsuccessEmailLists=new ArrayList<>();
        }
        //

       Date dates =getStartTime();
       SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        EmailDto emailDto=new EmailDto();
        //????????????
        emailDto.setSubject("??????????????????"+customCode+simpleDateFormat.format(dates));

        emailDto.setText("?????????????????????"+customCode+"??????????????????????????????????????????");

        //????????????
        String empTOs[] =email.split(",");
        List<String> emaillist1= Arrays.asList(empTOs);
        for (int i=0; i<emaillist1.size();i++){
            //???????????????
            emailDto.setTo(emaillist1.get(i));

            List<EmailDelOutboundError> emailDelOutboundErrorList= BeanMapperUtil.mapList(delOutbounderrorEmailLists, EmailDelOutboundError.class);
            emailDto.setEmailDelOutboundErrorList(emailDelOutboundErrorList);

            List<EmailDelOutboundSuccess> emailDelOutboundSuccessList= BeanMapperUtil.mapList(delOutboundsuccessEmailLists, EmailDelOutboundSuccess.class);
            emailDto.setEmailDelOutboundSuccessList(emailDelOutboundSuccessList);

            if(customCode!=null&&!customCode.equals("")){
                R r= emailFeingService.sendEmaildelOut(emailDto);

            }
        }



    }

    public void ExcleDelOutboundBatchUpdateTrackingTwo( List<DelOutbounderrorEmail> delOutbounderrorEmailLists,List<DelOutboundsuccessEmail> delOutboundsuccessEmailLists){
        logger.info("??????????????????delOutbounderrorEmailLists???{}",delOutbounderrorEmailLists);
        logger.info("??????????????????delOutboundsuccessEmailLists???{}",delOutboundsuccessEmailLists);

        if (delOutbounderrorEmailLists==null){
            delOutbounderrorEmailLists=new ArrayList<>();
        }
        if (delOutboundsuccessEmailLists==null){
            delOutboundsuccessEmailLists=new ArrayList<>();
        }
        //

        Date dates =getStartTime();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        EmailDto emailDto=new EmailDto();
        //????????????
        emailDto.setSubject("??????????????????"+simpleDateFormat.format(dates));

        emailDto.setText("?????????"+"??????????????????????????????????????????");




            //???????????????
            emailDto.setTo("developer@dmfgroup.net");

            List<EmailDelOutboundError> emailDelOutboundErrorList= BeanMapperUtil.mapList(delOutbounderrorEmailLists, EmailDelOutboundError.class);
            emailDto.setEmailDelOutboundErrorList(emailDelOutboundErrorList);

            List<EmailDelOutboundSuccess> emailDelOutboundSuccessList= BeanMapperUtil.mapList(delOutboundsuccessEmailLists, EmailDelOutboundSuccess.class);
            emailDto.setEmailDelOutboundSuccessList(emailDelOutboundSuccessList);

                R r= emailFeingService.sendEmaildelOut(emailDto);






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

    //?????????????????????
//    public static Long getBeginDayOfYesterday() {
//        Calendar cal = new GregorianCalendar();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);
//        cal.add(Calendar.DAY_OF_MONTH, -1);
//        return cal.getTimeInMillis();
//    }



    //?????????????????????
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
