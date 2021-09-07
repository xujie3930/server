package com.szmsd.finance.service;

import com.szmsd.finance.domain.FssRefundRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.finance.dto.ConfirmOperationDTO;
import com.szmsd.finance.dto.RefundRequestDTO;
import com.szmsd.finance.dto.RefundRequestListDTO;
import com.szmsd.finance.dto.RefundRequestQueryDTO;
import com.szmsd.finance.enums.RefundStatusEnum;
import com.szmsd.finance.enums.ReviewStatusEnum;
import com.szmsd.finance.vo.RefundRequestListVO;
import com.szmsd.finance.vo.RefundRequestVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 退费记录表 服务类
 * </p>
 *
 * @author 11
 * @since 2021-08-13
 */
public interface IRefundRequestService extends IService<FssRefundRequest> {

    /**
     * 查询退款申请列表
     *
     * @param queryDTO
     * @return
     */
    List<RefundRequestListVO> selectRequestList(RefundRequestQueryDTO queryDTO);

    RefundRequestVO selectDetailInfoById(String id);

    int insertRefundRequest(RefundRequestListDTO addDTO);

    int insertBatchRefundRequest(List<RefundRequestDTO> addList);

    int updateRefundRequest(RefundRequestDTO updateDTO);

    int deleteRefundRequestByIds(List<String> ids);

    int importByTemplate(MultipartFile file);

    int approve(RefundStatusEnum status, List<String> ids);

    int confirmOperation(ConfirmOperationDTO confirmOperationDTO);
}

