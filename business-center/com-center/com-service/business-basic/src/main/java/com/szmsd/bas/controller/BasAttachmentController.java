package com.szmsd.bas.controller;

import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentExcelDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.domain.dto.BasMultiplePiecesDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.domain.BasArea;
import com.szmsd.bas.domain.dto.BasAttachmentDTO;
import com.szmsd.bas.domain.dto.FileDTO;
import com.szmsd.bas.service.IBasAttachmentService;
import com.szmsd.bas.util.FileUtil;
import com.szmsd.bas.util.PdfUtil;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
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
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private IBasAttachmentService basAttachmentService;

    @Autowired
    public BasAttachmentController(Environment env) {
        this.env = env;
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询 - bas:attachment:list", notes = "查询")
    public R<List<BasAttachment>> list(BasAttachmentQueryDTO queryDTO) {
        List<BasAttachment> list = basAttachmentService.selectList(queryDTO);
        return R.ok(list);
    }
    @PreAuthorize("@ss.hasPermi('bas:attachment:page')")
    @GetMapping("/page")
    @ApiOperation(value = "分页查询 - bas:attachment:page", notes = "分页查询")
    public TableDataInfo page(BasAttachmentQueryDTO queryDTO) {
        startPage(queryDTO);
        List<BasAttachment> list = basAttachmentService.selectPageList(queryDTO);
        return getDataTable(list);
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

    @PreAuthorize("@ss.hasPermi('bas:attachment:update')")
    @PostMapping("/update")
    @ApiOperation(value = "修改附件表 - bas:attachment:update", notes = "修改附件表")
    public R update(@RequestBody List<BasAttachment> list) {
        basAttachmentService.updateBatchById(list);
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
            throw new CommonException("999", "Attachment cannot be empty！");
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

    private List<BasAttachmentDataDTO> processBoxMark(MultipartFile multipartFile, AttachmentTypeEnum attachmentTypeEnum){
        List<BasAttachmentDataDTO> filesUrl = new ArrayList<>();
        PdfUtil.toMonyFile(multipartFile).forEach(map -> {
            MultipartFile myFile = (MultipartFile)map.get("multipartFile");
            String barCode = (String)map.get("barCode");
            Files files = FileUtil.getFileUrl(new FileDTO()
                    .setUrl(env.getProperty("file.url"))
                    .setMyFile(myFile)
                    .setUploadFolder(env.getProperty("file.uploadFolder"))
                    .setType(attachmentTypeEnum)
                    .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
            String url = files.getUrl();

            filesUrl.add(new BasAttachmentDataDTO().setAttachmentName(files.getFileName()).setAttachmentType(attachmentTypeEnum.getAttachmentType()).setAttachmentUrl(url)
                    .setRemark(barCode));
        });
        return filesUrl;
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:uploadMultiplePieces')")
    @ApiOperation(httpMethod = "POST", value = "一票多件附件上传 - bas:uploadMultiplePieces:uploadMultiplePieces - swagger接收不到文件", notes = "一票多件附件上传")
    @PostMapping(value = "/uploadMultiplePieces", headers = "content-type=multipart/form-data")
    @ApiImplicitParams({@ApiImplicitParam(name = "attachmentTypeEnum", value = "附件类型", required = true)})
    public R<List<BasMultiplePiecesDataDTO>> uploadMultiplePieces(@RequestParam("attachmentUrl") MultipartFile[] myFiles,
                                                                  @RequestParam("attachmentTypeEnum") AttachmentTypeEnum attachmentTypeEnum) {

        if(attachmentTypeEnum == AttachmentTypeEnum.MULTIPLE_PIECES_BOX_MARK || attachmentTypeEnum == AttachmentTypeEnum.BULK_ORDER_BOX){

            List<BasMultiplePiecesDataDTO> filesUrl = new ArrayList<>();
            List<MultipartFile> multipartFiles = Arrays.asList(myFiles);
            if (CollectionUtils.isEmpty(multipartFiles)) {
                throw new CommonException("999", "Attachment cannot be empty！");
            }
            multipartFiles.forEach(myFile -> {
                Files files = FileUtil.getFileUrl(new FileDTO()
                        .setUrl(env.getProperty("file.url"))
                        .setMyFile(myFile)
                        .setUploadFolder(env.getProperty("file.uploadFolder"))
                        .setType(attachmentTypeEnum)
                        .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
                String url = files.getUrl();

                BasMultiplePiecesDataDTO dto = new BasMultiplePiecesDataDTO();
                filesUrl.add(dto.setAttachmentName(files.getFileName()).setAttachmentType(attachmentTypeEnum.getAttachmentType()).setAttachmentUrl(url));
                //处理箱标数据
                dto.setList(this.processBoxMark(myFile, attachmentTypeEnum));
            });
            return R.ok(filesUrl);
        }else{
            List<BasMultiplePiecesDataDTO> filesUrl = new ArrayList<>();
            List<MultipartFile> multipartFiles = Arrays.asList(myFiles);
            if (CollectionUtils.isEmpty(multipartFiles)) {
                throw new CommonException("999", "Attachment cannot be empty！");
            }
            multipartFiles.forEach(myFile -> {
                Files files = FileUtil.getFileUrl(new FileDTO()
                        .setUrl(env.getProperty("file.url"))
                        .setMyFile(myFile)
                        .setUploadFolder(env.getProperty("file.uploadFolder"))
                        .setType(attachmentTypeEnum)
                        .setMainUploadFolder(env.getProperty("file.mainUploadFolder")));
                String url = files.getUrl();
                filesUrl.add(new BasMultiplePiecesDataDTO().setAttachmentName(files.getFileName()).setAttachmentType(attachmentTypeEnum.getAttachmentType()).setAttachmentUrl(url));
            });
            return R.ok(filesUrl);
        }

    }


    @PreAuthorize("@ss.hasPermi('bas:attachment:uploadMultiplePiecesSave')")
    @ApiOperation(httpMethod = "POST", value = "附件上传及保存 - bas:uploadMultiplePiecesSave:uploadMultiplePieces - swagger接收不到文件", notes = "附件上传及保存")
    @PostMapping(value = "/uploadMultiplePiecesSave", headers = "content-type=multipart/form-data")
    @ApiImplicitParams({@ApiImplicitParam(name = "attachmentTypeEnum", value = "附件类型", required = true)})
    public synchronized R<List<BasAttachmentExcelDTO>> uploadMultiplePiecesSave(@RequestParam("attachmentUrl") MultipartFile[] myFiles,
                                         @RequestParam("attachmentTypeEnum") AttachmentTypeEnum attachmentTypeEnum, HttpServletResponse response) {
        List<BasAttachmentExcelDTO> filesUrl = new ArrayList<>();
        List<MultipartFile> multipartFiles = Arrays.asList(myFiles);
        if (CollectionUtils.isEmpty(multipartFiles)) {
            throw new CommonException("999", "Attachment cannot be empty！");
        }
        multipartFiles.forEach(myFile -> {
            List<BasAttachmentDataDTO> list = this.processBoxMark(myFile, attachmentTypeEnum);


            List<String> ids = list.stream().map(BasAttachmentDataDTO::getRemark).filter(Objects::nonNull).collect(Collectors.toList());
            java.util.Map<String, BasAttachment> map = new HashMap();
            if(ids.size() > 0){
                BasAttachmentQueryDTO queryDTO = new BasAttachmentQueryDTO();
                queryDTO.setBusinessNoList(ids);
                queryDTO.setAttachmentType(attachmentTypeEnum.getAttachmentType());
                List<BasAttachment> basAttachments = basAttachmentService.selectList(queryDTO);

                for (BasAttachment vo: basAttachments){
                    map.put(vo.getBusinessNo(), vo);
                }
            }


            for(int i = 0; i < list.size(); i++){
                BasAttachmentDataDTO dto = list.get(i);
                BasAttachmentExcelDTO dto1 = new BasAttachmentExcelDTO();
                dto1.setBusinessNo(dto.getRemark());
                dto1.setBusinessItem(""+(i+1));
                filesUrl.add(dto1);
                BasAttachment dataBasAttachment = map.get(dto1.getBusinessNo());
                if(dataBasAttachment != null){
                    throw new CommonException("999", dto1.getBusinessNo()+"The box label has been imported,Do not repeat the operation");
                }
                basAttachmentService.insert(dto.getRemark(), ""+(i+1), Arrays.asList(dto.getAttachmentUrl()), attachmentTypeEnum, "");


            }
        });

        return R.ok(filesUrl);
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:downMultiplePieces')")
    @ApiOperation(httpMethod = "POST", value = "附件Excel下载 - bas:downMultiplePieces:downMultiplePieces ", notes = "附件下载")
    @PostMapping(value = "/downMultiplePieces")
    public void downMultiplePieces(@RequestBody List<BasAttachmentExcelDTO> list, HttpServletResponse response) {
        ExcelUtil<BasAttachmentExcelDTO> util = new ExcelUtil<BasAttachmentExcelDTO>(BasAttachmentExcelDTO.class);
        util.exportExcel(response, list, "attachment");
    }

}
