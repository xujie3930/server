package com.szmsd.bas.controller;


import com.google.common.collect.Lists;
import com.szmsd.bas.domain.BasFile;
import com.szmsd.bas.domain.BasFileDao;
import com.szmsd.bas.service.BasFileService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.BatchDownFilesUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;


@Api(tags = {"文件中心下载"})
@RestController
@RequestMapping("/basFile")
@Slf4j
public class BasFileController extends BaseController {
    @Autowired
    private BasFileService basFileService;

    @Value("${filepath}")
    private String filepath;

    @PostMapping("/list")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public TableDataInfo<BasFile> list(@RequestBody BasFileDao basFile)
    {
        startPage(basFile);
        LoginUser loginUser =SecurityUtils.getLoginUser();
        basFile.setCreateBy(loginUser.getUsername());
        List<BasFile> list = basFileService.selectBasFile(basFile);
        return getDataTable(list);
    }

    @PostMapping("/listmodularName")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public R<List<String>> listmodularName()
    {

        R r = basFileService.listmodularName();
        return r;
    }

    @PostMapping("/addbasFile")
    @ApiOperation(value = "新增",notes = "新增")
    public R addbasFile(@RequestBody BasFile basFile) {

        R r = basFileService.addbasFile(basFile);
        return r;
    }

    @PostMapping("/updatebasFile")
    @ApiOperation(value = "修改",notes = "修改")
    public R updatebasFile(@RequestBody BasFile basFile) {

        R r = basFileService.updatebasFile(basFile);
        return r;
    }




    /**
     * 单个多个都可以下载
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @GetMapping("/download")
    @ApiOperation(value = "文件下载功能",notes = "文件下载功能")
    public R downloads(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            String filePath = filepath;
            // 取得文件名。
            String filenames = request.getParameter("filename");
            String[] filenam = {};
            if (filenames != null && !filenames.equals("")) {
                filenam = filenames.split(",");
            }

                if (filenam.length > 1) {
                    List<File> fileList = Lists.newArrayList(); //文件的集合
                    //zip名字
                    String zipName = request.getParameter("zipName");
                    String zipx = ".zip";
                    //前端还没传自己先测
                    String zipFilePath = zipName + zipx; //zip缓存的位置，方法工具类中方法下载完成后删除缓存zip
                    for (String filename : filenam) {
                        String path = filePath + filename;
                        log.info("文件下载地址：{}",path);
                        File file = new File(path);
                        fileList.add(file);
                    }
                    BatchDownFilesUtils.downLoadFiles(fileList, zipFilePath, request, response); //调用下载方法
                } else {
                    if (filenam.length < 1) {
                      return R.failed();
                    } else {

                        //单个下载
                        BatchDownFilesUtils.download(request, response, filePath); //调用下载方法
                    }
                }

        } catch (Exception e) {
            return R.failed();
        }

        return R.ok();
    }
}
