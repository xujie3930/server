package com.szmsd.chargerules.service.impl;

import cn.hutool.core.date.DateUtil;
import com.szmsd.chargerules.service.ICustomPricesService;
import com.szmsd.chargerules.service.IGradeService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.web.page.PageVO;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.http.api.feign.HtpCustomPricesFeignService;
import com.szmsd.http.api.feign.HtpGradeFeignService;
import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.dto.chaLevel.ChaLevelMaintenanceDto;
import com.szmsd.http.dto.custom.*;
import com.szmsd.http.dto.grade.*;
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
public class GradeServiceImpl implements IGradeService {
    @Resource
    private HtpGradeFeignService htpGradeFeignService;

    @Override
    public R<GradeMainDto> detailResult(String id) {
        return htpGradeFeignService.detailResult(id);
    }

    @Override
    public TableDataInfo<GradeMainDto> page(GradePageRequest pageDTO) {

        R<PageVO<GradeMainDto>> r = htpGradeFeignService.page(pageDTO);
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
    public R detailImport(UpdateGradeDetailDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Operation userIdentity = new Operation();
        userIdentity.setTime(DateUtil.formatTime(new Date()));
        userIdentity.setOperator(new Operator()
                .setCode(loginUser.getUserId().toString()).
                setName(loginUser.getUsername()));
        dto.setUserIdentity(userIdentity);
        return htpGradeFeignService.detailImport(dto);
    }

    @Override
    public R customUpdate(UpdateGradeCustomDto dto) {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Operation userIdentity = new Operation();
        userIdentity.setTime(DateUtil.formatTime(new Date()));
        userIdentity.setOperator(new Operator()
                .setCode(loginUser.getUserId().toString()).
                setName(loginUser.getUsername()));
        dto.setUserIdentity(userIdentity);
        return htpGradeFeignService.customUpdate(dto);
    }

    @Override
    public R create(MergeGradeDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);

        return htpGradeFeignService.create(dto);
    }

    @Override
    public R update(MergeGradeDto dto) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        UserIdentity userIdentity = new UserIdentity();
        userIdentity.setUserId(loginUser.getUserId().toString());
        userIdentity.setUserName(loginUser.getUsername().toString());
        userIdentity.setFullName(loginUser.getUsername().toString());
        dto.setUserIdentity(userIdentity);
        return htpGradeFeignService.update(dto);
    }
}

