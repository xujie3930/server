package com.szmsd.chargerules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.api.feign.HtpDiscountFeignService;
import com.szmsd.http.api.feign.HtpGradeFeignService;
import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.vo.Operation;
import com.szmsd.http.vo.Operator;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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

    @Override
    public R updateDiscountDetail(CustomDiscountMainDto dto) {
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
    public R<CustomPricesPageDto> result(String clientCode) {
        return htpCustomPricesFeignService.page(clientCode);
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
        return htpGradeFeignService.detailResult(id);
    }

    @Override
    public R gradeDetailResult(String id) {
        return htpDiscountFeignService.detailResult(id);
    }
}

