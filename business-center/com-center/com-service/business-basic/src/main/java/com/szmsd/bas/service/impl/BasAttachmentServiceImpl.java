package com.szmsd.bas.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.BasRegion;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.dao.BasAttachmentMapper;
import com.szmsd.bas.domain.dto.BasAttachmentDTO;
import com.szmsd.bas.service.IBasAttachmentService;
import com.szmsd.bas.util.FileUtil;
import com.szmsd.common.core.enums.ExceptionMessageEnum;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.text.UUID;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.bean.QueryWrapperUtil;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 附件表 服务实现类
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Service
public class BasAttachmentServiceImpl extends ServiceImpl<BasAttachmentMapper, BasAttachment> implements IBasAttachmentService {

    protected static final Logger log = LoggerFactory.getLogger(BasAttachmentServiceImpl.class);

    private final Environment env;

    @Autowired
    public BasAttachmentServiceImpl(Environment env) {
        this.env = env;
    }

    @Override
    public BasAttachment selectById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    public List<BasAttachment> selectList(BasAttachmentQueryDTO queryDto) {
        String businessNo = queryDto.getBusinessNo();
        if (StringUtils.isNotBlank(businessNo)){
            queryDto.setBusinessNo(null);
            queryDto.setBusinessNoList( StringToolkit.getCodeByArray(businessNo));
        }
        return baseMapper.selectList(queryDto);
    }

    @Override
    public List<BasAttachment> selectPageList(BasAttachmentQueryDTO queryDto) {
        QueryWrapper<BasAttachment> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.filter(queryWrapper, SqlKeyword.EQ, "businessCode", queryDto.getBusinessCode());
        queryWrapper.orderByDesc("create_time");
        return baseMapper.selectList(queryDto);
    }

    /**
     * 新增
     *
     * @param businessNo 业务编号
     * @param filesUrl 文件路径 - 多文件
     * @param attachmentTypeEnum 文件上传业务枚举
     */
    @Override
    public void insert(String businessNo, String businessItemNo, List<String> filesUrl, AttachmentTypeEnum attachmentTypeEnum, String remark) {
        log.info("保存附件：{}, {}, {}, {}", businessNo, businessItemNo, filesUrl, attachmentTypeEnum);
        if (CollectionUtils.isEmpty(filesUrl)) {
            log.info("保存附件：附件地址为空");
            return;
        }
        filesUrl.forEach(fileUrl -> {
            BasAttachment basAttachment = new BasAttachment();
            basAttachment.setBusinessCode(attachmentTypeEnum.getBusinessCode());
            basAttachment.setBusinessType(attachmentTypeEnum.getBusinessType());
            basAttachment.setBusinessNo(businessNo);
            Optional.ofNullable(businessItemNo).ifPresent(item -> basAttachment.setBusinessItemNo(item));
            basAttachment.setAttachmentType(attachmentTypeEnum.getAttachmentType());
            basAttachment.setAttachmentId(Math.abs(UUID.getRandom().nextInt()));
            basAttachment.setAttachmentName(FileUtil.getFileName(fileUrl));
            basAttachment.setAttachmentPath(env.getProperty("file.mainUploadFolder") + FileUtil.getFileRelativePath(fileUrl));
            basAttachment.setAttachmentUrl(fileUrl);
            basAttachment.setAttachmentFormat(FileUtil.getFileSuffix(fileUrl));
            basAttachment.setRemark(remark);
            baseMapper.insert(basAttachment);
        });
        log.info("保存附件：保存成功");
    }

    @Override
    public void insertList(String businessNo, String businessItemNo, List<BasAttachmentDataDTO> fileList, AttachmentTypeEnum attachmentTypeEnum) {
        List<String> filesUrl = fileList.stream().map(BasAttachmentDataDTO::getAttachmentUrl).collect(Collectors.toList());
        insert(businessNo, businessItemNo, filesUrl, attachmentTypeEnum, null);
    }

    /**
     * 新增
     *
     * @param basAttachmentDto 附件表 - 数据传输对象
     */
    @Override
    public void insert(BasAttachmentDTO basAttachmentDto) {
        this.insertList(basAttachmentDto.getBusinessNo(), basAttachmentDto.getBusinessItemNo(), basAttachmentDto.getFileList(), basAttachmentDto.getAttachmentTypeEnum());
    }

    /**
     * 删除及新增，该方法会自动对比数据库和前端传值，前端传的新值会新增，不存在的值会删除
     *
     * @param basAttachmentDto 附件表 - 数据传输对象
     */
    @Override
    @Transactional
    public void saveAndUpdate(BasAttachmentDTO basAttachmentDto) {
        String businessNo = basAttachmentDto.getBusinessNo();
        String businessItemNo = basAttachmentDto.getBusinessItemNo();
        AssertUtil.isTrue(StringUtils.isNotEmpty(businessNo), ExceptionMessageEnum.CANNOTBENULL, "businessNo");
        AttachmentTypeEnum attachmentTypeEnum = basAttachmentDto.getAttachmentTypeEnum();
        AssertUtil.notNull(attachmentTypeEnum, ExceptionMessageEnum.CANNOTBENULL, "attachmentTypeEnum");
        List<BasAttachmentDataDTO> fileList = basAttachmentDto.getFileList();

        // 过滤出已持久化的附件
        List<Integer> updates = ListUtils.emptyIfNull(fileList).stream().filter(e -> e.getId() != null).map(BasAttachmentDataDTO::getId).collect(Collectors.toList());

        // 查询已有
        BasAttachmentQueryDTO basAttachmentQueryDto = new BasAttachmentQueryDTO().setBusinessNo(businessNo).setBusinessItemNo(businessItemNo).setAttachmentType(attachmentTypeEnum.getAttachmentType());
        List<BasAttachment> basAttachments = selectList(basAttachmentQueryDto);
        List<Integer> alreadyIds = ListUtils.emptyIfNull(basAttachments).stream().map(BasAttachment::getId).collect(Collectors.toList());
        alreadyIds.removeAll(updates);

        // 删除
        if (CollectionUtil.isNotEmpty(alreadyIds)) {
            alreadyIds.forEach(this::deleteById);
        }

        //传值为空的 就进行新增操作
        List<String> collect = ListUtils.emptyIfNull(fileList).stream().filter(e -> e.getId() == null).map(BasAttachmentDataDTO::getAttachmentUrl).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            insert(businessNo, basAttachmentDto.getBusinessItemNo(), collect, attachmentTypeEnum, null);
        }

    }

    /**
     * 根据id删除单个文件
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
        log.info("附件删除id：{}", id);
        BasAttachment basAttachment = baseMapper.selectById(id);
        Optional.ofNullable(basAttachment).ifPresent(item -> {
            baseMapper.deleteById(id);
            FileUtil.deleteFile(item.getAttachmentPath() + "/" + item.getAttachmentName() + item.getAttachmentFormat());
        });
        log.info("附件删除id, 删除完成");
    }

    /**
     * 根据业务编号删除文件
     */
    @Override
    public void deleteByBusinessNo(String businessNo, String businessItemNo, String attachmentType) {
        log.info("附件删除businessNo：{}, attachmentType: {}", businessNo, attachmentType);
        List<BasAttachment> basAttachments = this.selectList(new BasAttachmentQueryDTO().setBusinessNo(businessNo).setBusinessItemNo(businessItemNo).setAttachmentType(attachmentType));
        basAttachments.forEach(item -> {
            baseMapper.deleteById(item.getId());
            FileUtil.deleteFile(item.getAttachmentPath() + "/" + item.getAttachmentName() + item.getAttachmentFormat());
        });
        log.info("附件删除businessNo, 删除完成");
    }

}

