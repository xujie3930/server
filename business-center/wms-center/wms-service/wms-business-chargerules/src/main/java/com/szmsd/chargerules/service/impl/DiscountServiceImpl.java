package com.szmsd.chargerules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.szmsd.chargerules.service.IDiscountService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpDiscountFeignService;
import com.szmsd.http.dto.OperationRecordDto;
import com.szmsd.http.dto.Packing;
import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.dto.discount.*;
import com.szmsd.http.dto.grade.GradeMainDto;
import com.szmsd.http.vo.Operation;
import com.szmsd.http.vo.Operator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
public class DiscountServiceImpl implements IDiscountService {
    @Resource
    private HtpDiscountFeignService htpDiscountFeignService;

    @Override
    public R<DiscountMainDto> detailResult(String id) {
        return htpDiscountFeignService.detailResult(id);
    }

    @Override
    public R<OperationRecordDto> operationRecord(String id) {
        return htpDiscountFeignService.operationRecord(id);
    }

    @Override
    public TableDataInfo<DiscountMainDto> page(DiscountPageRequest pageDTO) {

        R<PageVO<DiscountMainDto>> r = htpDiscountFeignService.page(pageDTO);
        if(r.getCode() == 200){
            TableDataInfo tableDataInfo = new TableDataInfo();
            tableDataInfo.setCode(r.getCode());
            tableDataInfo.setRows(r.getData().getData());
            tableDataInfo.setTotal(r.getData().getTotalRecords());
            return tableDataInfo;
        }else{
            throw new CommonException("400", r.getMsg());
        }
    }

    @Override
    public R detailImport(UpdateDiscountDetailDto dto) {


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
        return htpDiscountFeignService.detailImport(dto);
    }

    public static void savePackageLimit(DiscountDetailPackageLimitDto data){
        String minPackingLimit = data.getMinPackingLimitStr();
        if (StringUtils.isNotEmpty(minPackingLimit)) {
            String[] split = minPackingLimit.split("\\*");
            AssertUtil.isTrue(split.length == 3, "最小尺寸填写不符合规则(L*W*H)");
            CommonPackingLimit minPacking = getPacking(split, "最小尺寸填写不符合规则(L*W*H)");
            data.setMinPackingLimit(minPacking);
        }

        String packingLimit = data.getPackingLimitStr();
        if (StringUtils.isNotEmpty(packingLimit)) {
            String[] split = packingLimit.split("\\*");
            AssertUtil.isTrue(split.length == 3, "最小尺寸填写不符合规则(L*W*H)");
            CommonPackingLimit packing = getPacking(split, "最小尺寸填写不符合规则(L*W*H)");
            data.setPackingLimit(packing);
        }

    }
    private static CommonPackingLimit getPacking(String[] split, String throwMsg) {
        AssertUtil.isTrue(split.length == 3, throwMsg);
        try {
            return new CommonPackingLimit().setLength(new BigDecimal(split[0])).setWidth(new BigDecimal(split[1])).setHeight(new BigDecimal(split[2])).setLengthUnit("CM");
        } catch (Exception e) {
            throw new BaseException(throwMsg);
        }
    }

    @Override
    public R customUpdate(UpdateDiscountCustomDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Operation userIdentity = new Operation();
        userIdentity.setTime(DateUtil.formatTime(new Date()));
        userIdentity.setOperator(new Operator()
                .setCode(loginUser.getUserId().toString()).
                setName(loginUser.getUsername()));
        dto.setUserIdentity(userIdentity);
        return htpDiscountFeignService.customUpdate(dto);
    }

    @Override
    public R create(MergeDiscountDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);

        return htpDiscountFeignService.create(dto);
    }

    @Override
    public R update(MergeDiscountDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);
        return htpDiscountFeignService.update(dto);
    }
}

