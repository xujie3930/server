package com.szmsd.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.finance.config.ExportValid;
import com.szmsd.finance.config.FileVerifyUtil;
import com.szmsd.finance.domain.FssRefundRequest;
import com.szmsd.finance.dto.ConfirmOperationDTO;
import com.szmsd.finance.dto.RefundRequestDTO;
import com.szmsd.finance.dto.RefundRequestQueryDTO;
import com.szmsd.finance.enums.ReviewStatusEnum;
import com.szmsd.finance.mapper.RefundRequestMapper;
import com.szmsd.finance.service.IRefundRequestService;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.finance.vo.RefundRequestVO;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 退费记录表 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */
@Service
public class RefundRequestServiceImpl extends ServiceImpl<RefundRequestMapper, FssRefundRequest> implements IRefundRequestService {

    @Override
    public List<RefundRequestListVO> selectRequestList(RefundRequestQueryDTO queryDTO) {
        return baseMapper.selectRequestList(queryDTO);
    }

    @Override
    public RefundRequestVO selectDetailInfoById(String id) {
        FssRefundRequest fssRefundRequest = baseMapper.selectById(id);
        AssertUtil.notNull(fssRefundRequest, "数据不存在!");
        RefundRequestVO refundRequestVO = new RefundRequestVO();
        BeanUtils.copyProperties(fssRefundRequest, refundRequestVO);
        return refundRequestVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRefundRequest(RefundRequestDTO addDTO) {
        FssRefundRequest fssRefundRequest = new FssRefundRequest();
        BeanUtils.copyProperties(addDTO, fssRefundRequest);
        return baseMapper.insert(fssRefundRequest);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertBatchRefundRequest(List<RefundRequestDTO> addList) {
        List<FssRefundRequest> collect = addList.stream().map(x -> {
            FssRefundRequest fssRefundRequest = new FssRefundRequest();
            BeanUtils.copyProperties(x, fssRefundRequest);
            return fssRefundRequest;
        }).collect(Collectors.toList());
        return this.saveBatch(collect) ? addList.size() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateRefundRequest(RefundRequestDTO updateDTO) {
        AssertUtil.notNull(updateDTO.getId(), "id is require");
        FssRefundRequest fssRefundRequest = new FssRefundRequest();
        BeanUtils.copyProperties(updateDTO, fssRefundRequest);
        return baseMapper.updateById(fssRefundRequest);
    }

    @Override
    public int deleteRefundRequestByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return 1;
        return baseMapper.deleteBatchIds(ids);
    }

    @Override
    public int importByTemplate(MultipartFile file) {
        List<RefundRequestDTO> basPackingAddList = FileVerifyUtil.importExcel(file, RefundRequestDTO.class);
        handleInsertData(basPackingAddList, true);
        this.insertBatchRefundRequest(basPackingAddList);
        return 1;
    }

    public void handleInsertData(List<RefundRequestDTO> basPackingAddList, Boolean isExport) {
        AssertUtil.isTrue(CollectionUtils.isNotEmpty(basPackingAddList), "数据异常,请重新新增!");
        if (isExport) {
            //检验规则
            AtomicInteger importNo = new AtomicInteger(1);
            basPackingAddList.forEach(basSellAccountPeriodAddDTO -> FileVerifyUtil.validate(basSellAccountPeriodAddDTO, importNo, ExportValid.class));
        }
    }

    @Override
    public int approve(ReviewStatusEnum status, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) return 0;
        LoginUser loginUser = SecurityUtils.getLoginUser();

        // 审核退费
        return baseMapper.update(null, Wrappers.<FssRefundRequest>lambdaUpdate()
                .in(FssRefundRequest::getId, ids)
                .eq(FssRefundRequest::getAuditStatus, ReviewStatusEnum.AUDIT_WAIT.getStatus())

                .set(FssRefundRequest::getAuditStatus, status.getStatus())
                .set(FssRefundRequest::getAuditTime, LocalDateTime.now())
                .set(FssRefundRequest::getReviewerId, loginUser.getUserId())
                .set(FssRefundRequest::getReviewerCode, loginUser.getSellerCode())
                .set(FssRefundRequest::getReviewerName, loginUser.getUsername())
        );
    }

    @Override
    public int confirmOperation(ConfirmOperationDTO confirmOperationDTO) {
        //TODO 订单记录流水、【余额对应调增/调减】、【产生业务账记录】

        return 0;
    }
}

