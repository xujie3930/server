package com.szmsd.returnex.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.datascope.service.AwaitUserService;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.returnex.api.feign.serivice.IHttpFeignService;
import com.szmsd.returnex.config.BeanCopyUtil;
import com.szmsd.returnex.constant.ReturnExpressConstant;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.dto.*;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import com.szmsd.system.api.domain.SysUser;
import com.szmsd.system.api.model.UserInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName: ReturnExpressServiceImpl
 * @Description: 退货
 * @Author: 11
 * @Date: 2021/3/26 11:47
 */
@Slf4j
@Service
public class ReturnExpressServiceImpl extends ServiceImpl<ReturnExpressMapper, ReturnExpressDetail> implements IReturnExpressService {

    @Resource
    private ReturnExpressMapper returnExpressMapper;

    @Resource
    private IHttpFeignService httpFeignClient;

    @Resource
    private AwaitUserService awaitUserService;

    @Resource
    private BasFeignService basFeignService;

    private String getSellCode() {
        //UserInfo info = awaitUserService.info();
        UserInfo info = new UserInfo();
        Optional.ofNullable(info).map(UserInfo::getSysUser).map(SysUser::getSellerCode).orElseThrow(() -> new BaseException(""));
        info.setSysUser(new SysUser().setSellerCode("User11"));
        return "User11";
    }

    /**
     * 单号生成
     *
     * @return
     */
    public String genNo() {
        String code = getSellCode();
        log.info("调用自动生成单号：code={}", code);
        /*R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId("ck1").setCode(code));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);*/
        String s = System.currentTimeMillis() + "";
        log.info("调用自动生成单号：调用完成, {}-{}", code, s);
        return s;
    }

    /**
     * 新增退件单-生成预报单号
     *
     * @return 返回结果
     */
    @Override
    public String createExpectedNo() {
        return ReturnExpressConstant.RETURN_NO_KEY_PREFIX + genNo();
    }

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> selectReturnOrderList(ReturnExpressListQueryDTO queryDto) {

        //TODO 获取当前用户
        List<ReturnExpressDetail> returnExpressDetails = returnExpressMapper.selectList(Wrappers.<ReturnExpressDetail>lambdaQuery()
                .eq(StringUtil.isNotBlank(queryDto.getReturnType()), ReturnExpressDetail::getReturnType, queryDto.getReturnType())
                .eq(StringUtil.isNotBlank(queryDto.getApplyProcessMethod()), ReturnExpressDetail::getApplyProcessMethod, queryDto.getApplyProcessMethod())
                .between(queryDto.getCreateTimeStart() != null && queryDto.getCreateTimeEnd() != null, BaseEntity::getCreateTime, queryDto.getCreateTimeStart(), queryDto.getCreateTimeEnd())
                .eq(StringUtil.isNotBlank(queryDto.getWarehouseCode()), ReturnExpressDetail::getWarehouseCode, queryDto.getWarehouseCode())
                .in(CollectionUtils.isNotEmpty(queryDto.getForecastNumberList()), ReturnExpressDetail::getFromOrderNo, queryDto.getForecastNumberList())
                .in(CollectionUtils.isNotEmpty(queryDto.getReturnNoList()), ReturnExpressDetail::getReturnNo, queryDto.getReturnNoList())
                .eq(StringUtil.isNotBlank(queryDto.getSellerCode()), ReturnExpressDetail::getSellerCode, queryDto.getSellerCode())
                .isNull(queryDto.getNoUserQuery(), ReturnExpressDetail::getSellerCode)
        );
        return BeanCopyUtil.copyListProperties(returnExpressDetails, ReturnExpressListVO::new);
    }

    /**
     * 无名件管理列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> pageForNoUserBind(ReturnExpressListQueryDTO queryDto) {
        queryDto.setNoUserQuery(true);
        return selectReturnOrderList(queryDto);
    }

    /**
     * 无名件批量指派客户
     *
     * @param expressAssignDTO 指派条件
     * @return 返回结果
     */
    @Override
    public int assignUsersForNoUserBindBatch(ReturnExpressAssignDTO expressAssignDTO) {
        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .isNull(ReturnExpressDetail::getSellerCode)
                .in(ReturnExpressDetail::getId, expressAssignDTO.getIds())
                .set(ReturnExpressDetail::getSellerCode, expressAssignDTO.getSellerCode())
        );
        return update;
    }

    /**
     * 新建退件单
     *
     * @param returnExpressAddDTO 新增
     * @return 返回结果
     */
    @Override
    public int insertReturnExpressDetail(ReturnExpressAddDTO returnExpressAddDTO) {
       /* UserInfo info = awaitUserService.info();
        SysUser sysUser = info.getSysUser();
        String sellerCode = sysUser.getSellerCode();
        returnExpressAddDTO.setSellerCode(sellerCode);*/
        checkSubmit(returnExpressAddDTO);
        // 创建退报单 推给VMS仓库
        R<CreateExpectedRespVO> createExpectedRespVO = httpFeignClient.expectedCreate(returnExpressAddDTO.convertThis(CreateExpectedReqDTO.class));
        Optional.ofNullable(createExpectedRespVO).orElseThrow(() -> new BaseException("推送VMS仓库退单信息异常"));
        // 本地保存

        return saveReturnExpressDetail(returnExpressAddDTO.convertThis(ReturnExpressDetail.class));
    }

    private void checkSubmit(ReturnExpressAddDTO returnExpressAddDTO) {
        // 校验重复条件 TODO
        ReturnExpressDetail returnExpressDetail = returnExpressMapper.selectOne(Wrappers.<ReturnExpressDetail>lambdaQuery()
                .eq(ReturnExpressDetail::getExpectedNo, returnExpressAddDTO.getExpectedNo())
                .select(ReturnExpressDetail::getId));
        Optional.ofNullable(returnExpressDetail).ifPresent(x -> {
            throw new BaseException("请勿重复提交");
        });
    }

    /**
     * 本地保存用户发起的预报单数据
     *
     * @param returnExpressDetail
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public int saveReturnExpressDetail(ReturnExpressDetail returnExpressDetail) {
        return returnExpressMapper.insert(returnExpressDetail);
    }

    /**
     * 接收VMS仓库到件信息
     * /api/return/arrival #G1-接收仓库退件到货
     *
     * @param returnArrivalReqDTO 接收VMS仓库到件信息
     * @return 操作结果
     */
    @Override
    public int saveArrivalInfoFormVms(ReturnArrivalReqDTO returnArrivalReqDTO) {

        // 接收到件，状态未待用户确认
        if (returnArrivalReqDTO.getExpectedNo() != null) {
            int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                    .eq(ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .set(ReturnExpressDetail::getReturnNo, returnArrivalReqDTO.getReturnNo())
                    .set(ReturnExpressDetail::getFromOrderNo, returnArrivalReqDTO.getFromOrderNo())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getExpectedNo()), ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .set(ReturnExpressDetail::getScanCode, returnArrivalReqDTO.getScanCode())
                    .set(ReturnExpressDetail::getSellerCode, returnArrivalReqDTO.getSellerCode())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getRemark()), BaseEntity::getRemark, returnArrivalReqDTO.getRemark())
                    .set(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WAIT_CUSTOMER_DEAL)
            );
            return update;
        } else {
            // 新增无主件
            ReturnExpressDetail returnExpressDetail = returnArrivalReqDTO.convertThis(ReturnExpressDetail.class);
            returnExpressDetail.setDealStatus(ReturnExpressEnums.DealStatusEnum.WAIT_ASSIGNED);
            int insert = returnExpressMapper.insert(returnExpressDetail);
            // 其他处理

            return insert;
        }

    }

    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @Override
    public int updateProcessingInfoFromVms(ReturnProcessingReqDTO returnProcessingReqDTO) {
        boolean finish = returnProcessingReqDTO.getProcessType().equals(ReturnExpressEnums.ProcessTypeEnum.OpenAndCheck);
        ReturnExpressEnums.DealStatusEnum dealStatusEnum = finish ?
                ReturnExpressEnums.DealStatusEnum.WAIT_PROCESSED_AFTER_UNPACKING :
                ReturnExpressEnums.DealStatusEnum.VMS_FINISH;

        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo())
                .set(ReturnExpressDetail::getProcessType, returnProcessingReqDTO.getProcessType())
                .set(StringUtils.isNotBlank(returnProcessingReqDTO.getReturnNo()), BaseEntity::getRemark, returnProcessingReqDTO.getRemark())
                .set(ReturnExpressDetail::getApplyProcessMethod, returnProcessingReqDTO.getProcessType())
                .set(ReturnExpressDetail::getDealStatus, dealStatusEnum)
                .set(finish, ReturnExpressDetail::getFinishTime, LocalDateTime.now())
                .last("LIMIT 1")
        );

        return update;
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @Override
    public int updateExpressInfo(ReturnExpressAddDTO expressUpdateDTO) {
        AssertUtil.isTrue(expressUpdateDTO.getId() != null && expressUpdateDTO.getId() > 0, "更新异常！");
        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getId, expressUpdateDTO.getId())
                .set(expressUpdateDTO.getProcessType() != null, ReturnExpressDetail::getProcessType, expressUpdateDTO.getProcessType())
                .set(StringUtil.isNotBlank(expressUpdateDTO.getFromOrderNo()), ReturnExpressDetail::getFromOrderNo, expressUpdateDTO.getFromOrderNo())
        );
        // TODO 更新货物信息 货物信息未返回

        return update;
    }
}
