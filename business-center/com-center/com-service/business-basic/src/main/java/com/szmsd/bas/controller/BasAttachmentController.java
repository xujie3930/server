package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.domain.dto.BasAttachmentDTO;
import com.szmsd.bas.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.domain.dto.FileDTO;
import com.szmsd.bas.service.IBasAttachmentService;
import com.szmsd.bas.util.FileUtil;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 附件表 前端控制器
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Api(tags = {"附件表"})
@RestController
@RequestMapping("/bas-attachment")
public class BasAttachmentController extends BaseController {

    private final Environment env;

    @Autowired
    public BasAttachmentController(Environment env) {
        this.env = env;
    }

    @Resource
    private IBasAttachmentService basAttachmentService;

    @PreAuthorize("@ss.hasPermi('bas:attachment:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询 - bas:attachment:list", notes = "查询")
    public R<List<BasAttachment>> list(BasAttachmentQueryDTO queryDTO) {
        List<BasAttachment> list = basAttachmentService.selectList(queryDTO);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:list4feign')")
    @RequestMapping("/list4Feign")
    @ApiOperation(value = "查询 - bas:attachment:list4feign", notes = "查询")
    public R<List<BasAttachment>> list4Feign(@RequestBody BasAttachmentQueryDTO queryDTO) {
        List<BasAttachment> list = basAttachmentService.selectList(queryDTO);
        return R.ok(list);
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:saveAndUpdate')")
    @PostMapping("/saveAndUpdate")
    @ApiOperation(value = "保存附件表 - bas:attachment:saveAndUpdate", notes = "保存附件表")
    public R saveAndUpdate(@RequestBody BasAttachmentDTO basAttachmentDTO) {
        basAttachmentService.saveAndUpdate(basAttachmentDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:save')")
    @PostMapping("/save")
    @ApiOperation(value = "保存附件表 - bas:attachment:save", notes = "保存附件表")
    public R save(@RequestBody BasAttachmentDTO basAttachmentDTO) {
        basAttachmentService.insert(basAttachmentDTO);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:save')")
    @DeleteMapping("/deleteById/{id}")
    @ApiOperation(value = "删除 - 单个 - bas:attachment:publish", notes = "保存附件表")
    public R deleteById(@PathVariable("id") String id) {
        basAttachmentService.deleteById(Integer.valueOf(id));
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:save')")
    @DeleteMapping("/deleteByBusinessNo")
    @ApiOperation(value = "删除 - 多个 - bas:attachment:publish", notes = "保存附件表")
    public R deleteByBusinessNo(@RequestBody BasAttachmentDTO basAttachmentDTO) {
        basAttachmentService.deleteByBusinessNo(basAttachmentDTO.getBusinessNo(), basAttachmentDTO.getBusinessItemNo(), basAttachmentDTO.getAttachmentTypeEnum().getAttachmentType());
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:uploadattachment')")
    @ApiOperation(httpMethod = "POST", value = "附件上传 - bas:uploadattachment:uploadattachment - swagger接收不到文件", notes = "附件上传")
    @PostMapping(value = "/uploadAttachment", headers = "content-type=multipart/form-data")
    @ApiImplicitParams({@ApiImplicitParam(name = "attachmentTypeEnum", value = "附件类型", required = true),
            @ApiImplicitParam(name = "businessNo", value = "业务编号 - 业务场景：补充附件"),
            @ApiImplicitParam(name = "businessItemNo", value = "业务明细号 - 业务场景：补充附件"),})
    public R<List<BasAttachmentDataDTO>> uploadAttachment(@RequestParam("attachmentUrl") MultipartFile[] myFiles, @RequestParam("attachmentTypeEnum") AttachmentTypeEnum attachmentTypeEnum, String businessNo, String businessItemNo) {
        List<BasAttachmentDataDTO> filesUrl = new ArrayList<>();
        List<MultipartFile> multipartFiles = Arrays.asList(myFiles);
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new CommonException("999", "附件不能为空！");
        }
        multipartFiles.forEach(myFile -> {
            Files files = FileUtil.getFileUrl(new FileDTO()
                    .setUrl(env.getProperty("file.url"))
                    .setMyFile(myFile)
                    .setUploadFolder(env.getProperty("file.uploadFolder"))
                    .setType(attachmentTypeEnum)
                    .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
            String url = files.getUrl();
            filesUrl.add(new BasAttachmentDataDTO().setAttachmentName(files.getFileName()).setAttachmentType(attachmentTypeEnum.getAttachmentType()).setAttachmentUrl(url));
        });
        if (!"null".equalsIgnoreCase(businessNo) && StringUtils.isNotEmpty(businessNo)) {
            log.info("业务编号不为空 {} - 变更为补充附件 - 保存附件信息", businessNo);
            basAttachmentService.insert(new BasAttachmentDTO().setBusinessNo(businessNo).setBusinessItemNo(businessItemNo).setFileList(filesUrl).setAttachmentTypeEnum(attachmentTypeEnum));
            log.info("业务编号不为空 {} - 变更为补充附件 - 保存完成", businessNo);
            R r = R.ok(filesUrl);
            r.setMsg("附件补充完成，业务编号：" + businessNo);
            return r;
        }
        return R.ok(filesUrl);
    }

}
