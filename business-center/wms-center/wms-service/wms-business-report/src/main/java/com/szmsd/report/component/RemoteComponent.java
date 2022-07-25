package com.szmsd.report.component;

import cn.hutool.core.collection.ListUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.api.service.DelOutboundReportClientService;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import com.szmsd.exception.api.service.ExceptionInfoClientService;
import com.szmsd.finance.api.feign.RechargesFeignService;
import com.szmsd.finance.domain.AccountBalance;
import com.szmsd.finance.dto.AccountBalanceDTO;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import com.szmsd.putinstorage.domain.dto.InboundReceiptQueryDTO;
import com.szmsd.putinstorage.domain.vo.InboundCountVO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.feign.RemoteUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 远程接口
 *
 * @author liangchao
 * @date 2020/12/21
 */
@Component
@Slf4j
public class RemoteComponent {

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private RechargesFeignService rechargesFeignService;

    @Resource
    private InboundReceiptFeignService inboundReceiptFeignService;

    @Resource
    private DelOutboundReportClientService delOutboundReportClientService;

    @Resource
    private ExceptionInfoClientService exceptionInfoClientService;


    /**
     * 获取登录人信息
     *
     * @return
     */
    public SysUser getLoginUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        AssertUtil.notNull(loginUser, "登录过期, 请重新登录");
        SysUser sysUser = remoteUserService.queryGetInfoByUserId(loginUser.getUserId()).getData();
        return sysUser;
    }

    /**
     * 钱包查询
     * @param cusCode
     * @return
     */
    public List<AccountBalance> accountList(String cusCode) {
        R<List<AccountBalance>> accountBalanceR = null;
        try {
            AccountBalanceDTO accountBalanceDTO = new AccountBalanceDTO();
            accountBalanceDTO.setCusCode(cusCode);
            accountBalanceR = rechargesFeignService.accountList(accountBalanceDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (accountBalanceR == null) {
            return ListUtil.empty();
        }
        List<AccountBalance> data = accountBalanceR.getData();
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 入库单统计
     * @param queryDTO
     * @return
     */
    public List<InboundCountVO> inboundCount(InboundReceiptQueryDTO queryDTO) {
        R<List<InboundCountVO>> statistics = null;
        try {
            statistics = inboundReceiptFeignService.statistics(queryDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (statistics == null) {
            return ListUtil.empty();
        }
        List<InboundCountVO> data = statistics.getData();
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 查询提审数据 出库
     * @param queryDto
     * @return
     */
    public List<DelOutboundReportListVO> outboundReportCount(DelOutboundReportQueryDto queryDto) {
        List<DelOutboundReportListVO> data = null;
        try {
            data = delOutboundReportClientService.queryBringVerifyData(queryDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 查询出库数据
     * @param queryDto
     * @return
     */
    public List<DelOutboundReportListVO> outboundData(DelOutboundReportQueryDto queryDto) {
        List<DelOutboundReportListVO> data = null;
        try {
            data = delOutboundReportClientService.queryOutboundData(queryDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 查询出库创建数据
     * @param queryDto
     * @return
     */
    public List<DelOutboundReportListVO> queryCreateData(DelOutboundReportQueryDto queryDto) {
        List<DelOutboundReportListVO> data = null;
        try {
            data = delOutboundReportClientService.queryCreateData(queryDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null) {
            return ListUtil.empty();
        }
        return data;
    }

    /**
     * 问题件数量
     * @param cusCode
     * @return
     */
    public Integer problemCount(String cusCode) {
        Integer problemCount = 0;
        try {
            problemCount = exceptionInfoClientService.countprocessException(cusCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return problemCount;
    }

}
