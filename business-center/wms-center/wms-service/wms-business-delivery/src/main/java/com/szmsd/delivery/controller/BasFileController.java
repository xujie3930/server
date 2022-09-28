package com.szmsd.delivery.controller;


import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.szmsd.bas.domain.BasMaterial;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.utils.SecurityUtils;
import com.szmsd.delivery.domain.BasFile;
import com.szmsd.delivery.service.BasFileService;
import com.szmsd.delivery.task.BatchDownFilesUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = {"文件中心下载"})
@RestController
@RequestMapping("/basFileController")
@Slf4j
public class BasFileController extends BaseController {
    @Autowired
    private BasFileService basFileService;

    @Value("${filepaths}")
    private String filepath;

    @PostMapping("/list")
    @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
    public TableDataInfo list(@RequestBody BasFile basFile)
    {
        startPage();
        LoginUser loginUser =SecurityUtils.getLoginUser();
        basFile.setCreateBy(loginUser.getUsername());
        List<BasFile> list = basFileService.selectBasFile(basFile);
        return getDataTable(list);
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
