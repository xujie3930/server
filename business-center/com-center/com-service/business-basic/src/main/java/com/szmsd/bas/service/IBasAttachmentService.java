package com.szmsd.bas.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.domain.dto.BasAttachmentDTO;
import com.szmsd.bas.domain.dto.BasAttachmentDataDTO;

import java.util.List;

/**
 * <p>
 * 附件表 服务类
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
public interface IBasAttachmentService extends IService<BasAttachment> {

    /**
     * 主键查询
     *
     * @param id 主键ID
     * @return
     */
    BasAttachment selectById(String id);

    /**
     * 查询
     *
     * @param queryDto
     * @return BasAttachment模块集合
     */
    List<BasAttachment> selectList(BasAttachmentQueryDTO queryDto);

    /**
     * 新增
     *
     * @param businessNo            业务编号 非空
     * @param businessItemNo        业务编号
     * @param filesUrl              文件路径 - 多文件 非空
     * @param attachmentTypeEnum 文件上传业务枚举 非空
     */
    void insert(String businessNo, String businessItemNo, List<String> filesUrl, AttachmentTypeEnum attachmentTypeEnum);

    void insertList(String businessNo, String businessItemNo, List<BasAttachmentDataDTO> filesUrl, AttachmentTypeEnum attachmentTypeEnum);

    /**
     * 新增
     *
     * @param basAttachmentDTO 数据传输对象
     */
    void insert(BasAttachmentDTO basAttachmentDTO);

    /**
     * 根据id删除单个文件
     *
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据业务编号删除文件
     *
     * @param businessNo
     * @param attachmentType
     */
    void deleteByBusinessNo(String businessNo, String attachmentType);

    void saveAndUpdate(BasAttachmentDTO basAttachmentDTO);
}

