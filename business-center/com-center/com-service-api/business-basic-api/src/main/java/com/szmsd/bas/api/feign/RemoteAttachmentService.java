package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.api.factory.RemoteAttachmentServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 附件控制 BasAttachmentController
 *
 * @author szmsd
 */
@FeignClient(contextId = "remoteAttachmentService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = RemoteAttachmentServiceFallbackFactory.class)
public interface RemoteAttachmentService {

    /**
     * 查询
     *
     * @param queryDTO
     * @return
     */
    @RequestMapping(value = "/bas-attachment/list4Feign", method = RequestMethod.GET)
    R<List<BasAttachment>> list(@RequestBody BasAttachmentQueryDTO queryDTO);

    /**
     * 保存
     *
     * @param attachmentDTO
     * @return
     */
    @PostMapping(value = "/bas-attachment/save")
    @Deprecated
    R save(@RequestBody AttachmentDTO attachmentDTO);

    /**
     * 新增与修改共用逻辑
     *
     * @param attachmentDTO
     * @return
     */
    @PostMapping(value = "/bas-attachment/saveAndUpdate")
    R saveAndUpdate(@RequestBody AttachmentDTO attachmentDTO);


    /**
     * 删除附件
     *
     * @param attachmentDTO
     * @return
     */
    @DeleteMapping("/bas-attachment/deleteByBusinessNo")
    R deleteByBusinessNo(@RequestBody AttachmentDTO attachmentDTO);

    @PostMapping(value = "/bas-attachment/uploadAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    R<List<BasAttachmentDataDTO>> uploadAttachment(@RequestPart("attachmentUrl") MultipartFile[] myFiles,
                                                   @RequestParam("attachmentTypeEnum") AttachmentTypeEnum attachmentTypeEnum,
                                                   @RequestParam("businessNo") String businessNo,
                                                   @RequestParam("businessItemNo") String businessItemNo);

}
