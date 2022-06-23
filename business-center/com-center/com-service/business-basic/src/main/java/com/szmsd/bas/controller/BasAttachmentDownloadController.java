package com.szmsd.bas.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.api.domain.BasAttachment;
import com.szmsd.bas.api.domain.dto.BasAttachmentDataDTO;
import com.szmsd.bas.api.domain.dto.BasAttachmentQueryDTO;
import com.szmsd.bas.api.domain.dto.BasMultiplePiecesDataDTO;
import com.szmsd.bas.api.enums.AttachmentTypeEnum;
import com.szmsd.bas.domain.dto.BasAttachmentDTO;
import com.szmsd.bas.domain.dto.FileDTO;
import com.szmsd.bas.service.IBasAttachmentService;
import com.szmsd.bas.util.FileUtil;
import com.szmsd.bas.util.PdfUtil;
import com.szmsd.common.core.domain.Files;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.StringUtils;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 附件表 前端控制器
 * </p>
 *
 * @author liangchao
 * @since 2020-12-08
 */
@Api(tags = {"附件表下载"})
@Controller
@RequestMapping("/bas-attachment-download")
public class BasAttachmentDownloadController extends BaseController {

    private final Environment env;
    @Resource
    private IBasAttachmentService basAttachmentService;

    @Autowired
    public BasAttachmentDownloadController(Environment env) {
        this.env = env;
    }

    @PreAuthorize("@ss.hasPermi('bas:attachment:downloadByUrl')")
    @PostMapping("/downloadByParam")
    @ApiOperation(value = "下载 - bas:attachment:downloadByParam", notes = "下载")
    public void downloadByParam(@RequestBody List<String> url, HttpServletResponse response) throws IOException {
        if(url.size() == 0){
            throw new CommonException("999", "数据异常，附件信息不能控");
        }

        for(int i = 0; i < url.size(); i++){
            String str = url.get(i);
            int index = str.indexOf("/upload/ck1");
            if(index > -1){
                url.set(i, "/u01/www"+str.substring(index+7));
            }

        }
        BasAttachmentQueryDTO queryDTO = new BasAttachmentQueryDTO();
        queryDTO.setAttachmentUrl(url);
        List<BasAttachment> list = basAttachmentService.selectList(queryDTO);
        if(list.size() == 0){
            throw new CommonException("999", "数据异常，无效的附件信息");
        }
        List<String> newPath = new ArrayList<String>();
        for(BasAttachment item: list){
            newPath.add(item.getAttachmentPath() + "/" + item.getAttachmentName() + item.getAttachmentFormat());

        }
        response.setContentType("application/pdf");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" +
                new String("merge.pdf".getBytes("gb2312"), "iso8859-1"));
        PdfUtil.merge(response.getOutputStream(), newPath.toArray(new String[newPath.size()]));

    }

}
