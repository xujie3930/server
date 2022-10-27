package com.szmsd.chargerules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.szmsd.chargerules.mapper.BasProductServiceMapper;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.api.feign.HtpDiscountFeignService;
import com.szmsd.http.api.feign.HtpGradeFeignService;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.discount.DiscountDetailDto;
import com.szmsd.http.vo.Operation;
import com.szmsd.http.vo.Operator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
* <p>
    *  服务实现类
    * </p>
*
* @author admin
* @since 2022-06-22
*/
@Service
public class CustomPricesServiceImpl implements ICustomPricesService {

    @Resource
    private HtpCustomPricesFeignService htpCustomPricesFeignService;


    @Resource
    private HtpGradeFeignService htpGradeFeignService;


    @Resource
    private HtpDiscountFeignService htpDiscountFeignService;

    @Autowired
    private BasProductServiceMapper basProductServiceMapper;

    @Override
    public R<OperationRecordDto> operationRecord(String id) {
        return htpCustomPricesFeignService.operationRecord(id);
    }

    @Override
    public R updateDiscountDetail(CustomDiscountMainDto dto) {

        if(dto.getPricingDiscountRules() != null){
            for(DiscountDetailDto data: dto.getPricingDiscountRules()){
                if(data.getPackageLimit() == null){
                    continue;
                }
                DiscountServiceImpl.savePackageLimit(data.getPackageLimit());
            }
        }


        LoginUser loginUser = SecurityUtils.getLoginUser();
        Operation userIdentity = new Operation();
        userIdentity.setTime(DateUtil.formatTime(new Date()));
        userIdentity.setOperator(new Operator()
                .setCode(loginUser.getUserId().toString()).
                setName(loginUser.getUsername()));
        dto.setUserIdentity(userIdentity);
        return htpCustomPricesFeignService.updateDiscountDetail(dto);
    }

    @Override
    public R updateGradeDetail(CustomGradeMainDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Operation userIdentity = new Operation();
        userIdentity.setTime(DateUtil.formatTime(new Date()));
        userIdentity.setOperator(new Operator()
                .setCode(loginUser.getUserId().toString()).
                setName(loginUser.getUsername()));
        dto.setUserIdentity(userIdentity);
        return htpCustomPricesFeignService.updateGradeDetail(dto);
    }

    @Override
    public List<BasCustomPricesgradeDto> result(BasCustomPricesgradeDto basCustomPricesgradeDto) {
        if (basCustomPricesgradeDto.getSellerCode()!=null&&!basCustomPricesgradeDto.getSellerCode().equals("")){
            List<String> sellerCodeList = splitToArray(basCustomPricesgradeDto.getSellerCode(), "[\n,]");
            if (sellerCodeList.size()>0){
                basCustomPricesgradeDto.setSellerCodeList(sellerCodeList);
            }
        }
        List<BasCustomPricesgradeDto> basCustomPricesgradeDtoList=basProductServiceMapper.selectbasCustomPricesgradeList(basCustomPricesgradeDto);
        return basCustomPricesgradeDtoList;
    }

    public  List<String> splitToArray(String text, String split) {
        String[] arr = text.split(split);
        if (arr.length == 0) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<>();
        for (String s : arr) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            list.add(s);
        }
        return list;
    }

    @Override
    public R updateDiscount(UpdateCustomMainDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);
        return htpCustomPricesFeignService.updateDiscount(dto);
    }

    @Override
    public R updateGrade(UpdateCustomMainDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);


        return htpCustomPricesFeignService.updateGrade(dto);
    }

    @Override
    public R discountDetailResult(String id) {
        return htpDiscountFeignService.detailResult(id);
    }

    @Override
    public R gradeDetailResult(String id) {
        return htpGradeFeignService.detailResult(id);
    }
}

