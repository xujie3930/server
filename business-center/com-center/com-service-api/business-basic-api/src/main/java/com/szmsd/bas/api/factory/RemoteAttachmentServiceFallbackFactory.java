package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.AttachmentDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.feign.RemoteAttachmentService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 附件服务降级处理
 *
 * @author szmsd
 */
@Component
public class RemoteAttachmentServiceFallbackFactory implements FallbackFactory<RemoteAttachmentService> {
    private static final Logger log = LoggerFactory.getLogger(RemoteAttachmentServiceFallbackFactory.class);

    @Override
    public RemoteAttachmentService create(Throwable throwable) {
        return new RemoteAttachmentService() {

            @Override
            public R<List<BasAttachment>> list(BasAttachmentQueryDTO queryDTO) {
                log.error("附件查询失败, {}", throwable.getMessage());
                return R.failed(ExceptionMessageEnum.FAIL);
            }

            @Override
            public R save(AttachmentDTO attachmentDTO) {
                log.error("附件保存失败", throwable.getMessage());
                return R.failed(ExceptionMessageEnum.FAIL);
            }

            @Override
            public R saveAndUpdate(AttachmentDTO attachmentDTO) {
                log.error("附件保存失败", throwable.getMessage());
                return R.failed(ExceptionMessageEnum.FAIL);
            }

            @Override
            public R deleteByBusinessNo(String attachmentType, String businessNo) {
                log.error("附件删除失败", throwable.getMessage());
                return R.failed(ExceptionMessageEnum.FAIL);
            }
        };
    }
}
