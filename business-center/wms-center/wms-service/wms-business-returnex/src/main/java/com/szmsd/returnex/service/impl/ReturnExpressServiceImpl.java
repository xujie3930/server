package com.szmsd.returnex.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasCodeDto;
import com.szmsd.bas.api.feign.BasFeignService;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.common.datascope.service.AwaitUserService;
import com.szmsd.http.dto.returnex.CreateExpectedReqDTO;
import com.szmsd.http.dto.returnex.ProcessingUpdateReqDTO;
import com.szmsd.http.vo.returnex.CreateExpectedRespVO;
import com.szmsd.returnex.api.feign.client.IHttpFeignClientService;
import com.szmsd.returnex.constant.ReturnExpressConstant;
import com.szmsd.returnex.domain.ReturnExpressDetail;
import com.szmsd.returnex.domain.ReturnExpressGood;
import com.szmsd.returnex.dto.*;
import com.szmsd.returnex.enums.ReturnExpressEnums;
import com.szmsd.returnex.mapper.ReturnExpressMapper;
import com.szmsd.returnex.service.IReturnExpressGoodService;
import com.szmsd.returnex.service.IReturnExpressService;
import com.szmsd.returnex.vo.ReturnExpressListVO;
import com.szmsd.returnex.vo.ReturnExpressVO;
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
    private IHttpFeignClientService httpFeignClient;

    @Resource
    private IReturnExpressGoodService returnExpressGoodService;

    @Resource
    private AwaitUserService awaitUserService;

    @Resource
    private BasFeignService basFeignService;

    /**
     * TODO 开发使用 测试需要取消注释
     *
     * @return
     */
    private String getSellCode() {
        //UserInfo info = awaitUserService.info();
        UserInfo info = new UserInfo();
        Optional.ofNullable(info).map(UserInfo::getSysUser).map(SysUser::getSellerCode).orElseThrow(() -> new BaseException(""));
        info.setSysUser(new SysUser().setSellerCode("User11"));
        return "User11";
    }

    /**
     * 单号生成
     * TODO 开发使用 测试需要取消注释
     *
     * @return
     */
    public String genNo() {
        String code = ReturnExpressConstant.GENERATE_CODE;
        String appId = ReturnExpressConstant.GENERATE_APP_ID;
        log.info("调用自动生成单号：code={}", code);
        R<List<String>> r = basFeignService.create(new BasCodeDto().setAppId(appId).setCode(code));
        AssertUtil.notNull(r, "单号生成失败");
        AssertUtil.isTrue(r.getCode() == HttpStatus.SUCCESS, code + "单号生成失败：" + r.getMsg());
        String s = r.getData().get(0);
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
        return genNo();
    }

    /**
     * 退件单列表 - 分页
     *
     * @param queryDto 查询条件
     * @return 返回结果
     */
    @Override
    public List<ReturnExpressListVO> selectReturnOrderList(ReturnExpressListQueryDTO queryDto) {
        return returnExpressMapper.selectPageList(queryDto);
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
                .set(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WAIT_CUSTOMER_DEAL.getVal())
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
        CreateExpectedReqDTO createExpectedReqDTO = returnExpressAddDTO.convertThis(CreateExpectedReqDTO.class);
        createExpectedReqDTO.setRefOrderNo(returnExpressAddDTO.getFromOrderNo());
        //TODO 新增远程挂了
        //CreateExpectedRespVO createExpectedRespVO = httpFeignClient.expectedCreate(createExpectedReqDTO);
        //Optional.ofNullable(createExpectedRespVO).orElseThrow(() -> new BaseException("推送VMS仓库退单信息异常"));
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
        returnExpressDetail.setDealStatus(ReturnExpressEnums.DealStatusEnum.WMS_WAIT_RECEIVE.getVal());
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
    public int saveArrivalInfoFormWms(ReturnArrivalReqDTO returnArrivalReqDTO) {
        //TODO 操作上架的需要增加对应sku库存
        // 接收到件，状态未待用户确认
        if (returnArrivalReqDTO.getExpectedNo() != null) {
            int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                    .eq(ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .eq(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WMS_WAIT_RECEIVE.getVal())
                    .set(ReturnExpressDetail::getReturnNo, returnArrivalReqDTO.getReturnNo())
                    .set(ReturnExpressDetail::getFromOrderNo, returnArrivalReqDTO.getFromOrderNo())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getExpectedNo()), ReturnExpressDetail::getExpectedNo, returnArrivalReqDTO.getExpectedNo())
                    .set(ReturnExpressDetail::getScanCode, returnArrivalReqDTO.getScanCode())
                    .set(ReturnExpressDetail::getSellerCode, returnArrivalReqDTO.getSellerCode())
                    .set(StringUtil.isNotBlank(returnArrivalReqDTO.getRemark()), BaseEntity::getRemark, returnArrivalReqDTO.getRemark())
                    .set(ReturnExpressDetail::getArrivalTime, LocalDateTime.now())
                    .set(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WAIT_CUSTOMER_DEAL.getVal())
            );
            return update;
        } else {
            // 新增无主件 状态待指派
            ReturnExpressDetail returnExpressDetail = returnArrivalReqDTO.convertThis(ReturnExpressDetail.class);
            returnExpressDetail.setReturnSource(ReturnExpressEnums.ReturnSourceEnum.WMS_RETURN.getVal());
            returnExpressDetail.setDealStatus(ReturnExpressEnums.DealStatusEnum.WAIT_ASSIGNED.getVal());
            int insert = returnExpressMapper.insert(returnExpressDetail);
            // 其他处理
            return insert;
        }

    }

    /**
     * 批量 新增/修改sku
     *
     * @param list
     */
    private void addOrUpdateSkuList(List<ReturnExpressGood> list) {
        returnExpressGoodService.saveOrUpdateBatch(list);
    }


    /**
     * 接收VMS仓库退件处理结果
     * /api/return/processing #G2-接收仓库退件处理
     *
     * @param returnProcessingReqDTO 接收VMS仓库退件处理结果
     * @return 操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProcessingInfoFromWms(ReturnProcessingReqDTO returnProcessingReqDTO) {
        log.info("接收WMS仓库退件处理结果 {}", returnProcessingReqDTO);
        boolean needToContinue = returnProcessingReqDTO.getProcessType().equals(ReturnExpressEnums.ProcessTypeEnum.OpenAndCheck.getVal());
        ReturnExpressEnums.DealStatusEnum dealStatusEnum = needToContinue ?
                ReturnExpressEnums.DealStatusEnum.WAIT_PROCESSED_AFTER_UNPACKING :
                ReturnExpressEnums.DealStatusEnum.WMS_FINISH;

        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getReturnNo, returnProcessingReqDTO.getReturnNo())
                .eq(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WMS_RECEIVED_DEAL_WAY.getVal())
                .set(StringUtils.isNotBlank(returnProcessingReqDTO.getReturnNo()), BaseEntity::getRemark, returnProcessingReqDTO.getRemark())
                .set(ReturnExpressDetail::getApplyProcessMethod, returnProcessingReqDTO.getProcessType())
                .set(ReturnExpressDetail::getDealStatus, dealStatusEnum.getVal())
                .set(!needToContinue, ReturnExpressDetail::getFinishTime, LocalDateTime.now())
                .last("LIMIT 1")
        );
        log.info("接收WMS仓库退件处理结果 {} - 更新条数 {}", returnProcessingReqDTO, update);
        AssertUtil.isTrue(update == 1, "更新异常");
        return update;
    }

    /**
     * 更新退件单信息
     *
     * @param expressUpdateDTO 更新条件
     * @return 返回结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpressInfo(ReturnExpressAddDTO expressUpdateDTO) {
        log.info("更新退单信息 req:{}", expressUpdateDTO);
        AssertUtil.isTrue(expressUpdateDTO.getId() != null && expressUpdateDTO.getId() > 0, "更新异常！");
        int update = returnExpressMapper.update(new ReturnExpressDetail(), Wrappers.<ReturnExpressDetail>lambdaUpdate()
                .eq(ReturnExpressDetail::getId, expressUpdateDTO.getId())
                .eq(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WAIT_CUSTOMER_DEAL.getVal())
                .set(ReturnExpressDetail::getDealStatus, ReturnExpressEnums.DealStatusEnum.WMS_RECEIVED_DEAL_WAY.getVal())
                .set(expressUpdateDTO.getProcessType() != null, ReturnExpressDetail::getProcessType, expressUpdateDTO.getProcessType())
                .set(StringUtil.isNotBlank(expressUpdateDTO.getFromOrderNo()), ReturnExpressDetail::getFromOrderNo, expressUpdateDTO.getFromOrderNo())
                .last("LIMIT 1")
        );
        AssertUtil.isTrue(update == 1, "更新异常,请勿重复提交!");
        // TODO 更新货物信息 货物信息未返回无法更新sku货物信息
        httpFeignClient.processingUpdate(expressUpdateDTO.convertThis(ProcessingUpdateReqDTO.class));
        return update;
    }

    @Override
    public int expiredUnprocessedForecastOrder() {
        log.info("--------------更新过期未处理的预报单 开始--------------");
        int update = returnExpressMapper.update(null, Wrappers.<ReturnExpressDetail>lambdaUpdate());
        log.info("--------------更新过期未处理的预报单 结束--------------");
        return update;
    }

    @Override
    public ReturnExpressVO getInfo(Long id) {
        ReturnExpressDetail returnExpressDetail = returnExpressMapper.selectById(id);
        Optional.ofNullable(returnExpressDetail).orElseThrow(() -> new BaseException("数据不存在！"));
        return returnExpressDetail.convertThis(ReturnExpressVO.class);
    }
}
