package com.szmsd.bas.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.bas.domain.BasAttachment;
import com.szmsd.bas.domain.dto.BasAttachmentQueryDTO;

import java.util.List;

/**
 * <p>
 * 附件表 Mapper 接口
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
public interface BasAttachmentMapper extends BaseMapper<BasAttachment> {

    List<BasAttachment> selectList(BasAttachmentQueryDTO basAttachmentQueryDTO);
}
