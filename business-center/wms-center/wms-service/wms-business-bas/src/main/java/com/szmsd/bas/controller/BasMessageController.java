package com.szmsd.bas.controller;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.szmsd.bas.dto.BasMessageDto;
import com.szmsd.bas.dto.BasMessageQueryDTO;
import com.szmsd.common.core.utils.BatchDownFilesUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import com.szmsd.common.core.domain.R;
import org.springframework.web.bind.annotation.*;
import com.szmsd.bas.service.IBasMessageService;
import com.szmsd.bas.domain.BasMessage;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.core.web.page.TableDataInfo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.log.enums.BusinessType;
import io.swagger.annotations.Api;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.io.IOException;
import java.util.Random;

import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import com.szmsd.common.core.web.controller.BaseController;
import org.springframework.web.multipart.MultipartFile;


/**
* <p>
    *  前端控制器
    * </p>
*
* @author l
* @since 2021-04-25
*/


@Api(tags = {"消息通知"})
@RestController
@RequestMapping("/bas/message")
public class BasMessageController extends BaseController{

     @Resource
     private IBasMessageService basMessageService;

    @Value("${filepath}")
    private String filepath;
     /**
       * 查询模块列表
     */
      @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:list')")
      @GetMapping("/page")
      @ApiOperation(value = "查询模块列表",notes = "查询模块列表")
      public TableDataInfo list(BasMessageQueryDTO dto)
     {
            startPage();
            List<BasMessage> list = basMessageService.selectBasMessageList(dto);
            return getDataTable(list);
      }


    /**
     * 处理文件上传
     */
    @PostMapping(value = "/upload")
    public R uploading(@RequestParam("file") MultipartFile file) {
        JSONObject jsonObject = new JSONObject();
        //获取文件的后缀名
        String fileSub = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                .toLowerCase();
        if(!fileSubCheck(fileSub)){
            return R.failed();
        }
        Random d = new Random();
        String fileNames = file.getOriginalFilename();
        String fileName = fileNames.substring(0, fileNames.lastIndexOf("."));
        //获取文件大小MB
        DecimalFormat df= new DecimalFormat("0.00");
        String fileSizeString = df.format((double) file.getSize() / 1048576);

        //从新定义文件的名字(因为太长了不要uuid)
        //  String name = fileName+UUID.randomUUID().toString().replace("-", "") + d.nextInt(10000) + "" + fileSub;
        String name = fileName + "_" + d.nextInt(10000) + "" + fileSub;


        try {


            File targetFile = new File(filepath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }

            file.transferTo(new File(filepath, name));

        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传失败!");
            return R.failed();
        }

        log.info("文件上传成功!");
        jsonObject.put("fileName", name);
        jsonObject.put("fileSize", fileSizeString+"MB");
        return R.ok(jsonObject);
    }

    /**
     * 验证文件类型
     * @param fileSub
     * @return
     */
    public boolean fileSubCheck(String fileSub){
        String[] arr = {".jpg",".png",".jpeg",".pdf",".dwg",".dxf",".xls",".xlsx",".zip"};
        for (String s : arr) {
            if(fileSub.equals(s)){
                return true;
            }
        }
        return false;
    }

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

    /**
    * 导出模块列表
    */
     @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:export')")
     @Log(title = "模块", businessType = BusinessType.EXPORT)
     @GetMapping("/export")
     @ApiOperation(value = "导出模块列表",notes = "导出模块列表")
     public void export(HttpServletResponse response, BasMessageQueryDTO basMessage) throws IOException {
     List<BasMessage> list = basMessageService.selectBasMessageList(basMessage);
     ExcelUtil<BasMessage> util = new ExcelUtil<BasMessage>(BasMessage.class);
        util.exportExcel(response,list, "BasMessage");

     }

    /**
    * 获取模块详细信息
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:query')")
    @GetMapping(value = "getInfo/{id}")
    @ApiOperation(value = "获取模块详细信息",notes = "获取模块详细信息")
    public R getInfo(@PathVariable("id") Long id)
    {
    return R.ok(basMessageService.selectBasMessageById(id));
    }

    /**
    * 新增模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:add')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("add")
    @ApiOperation(value = "新增模块",notes = "新增模块")
    public R add(@RequestBody BasMessageDto basMessage)
    {
        basMessageService.insertBasMessage(basMessage);
        return R.ok();
    }

    /**
    * 修改模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:edit')")
    @Log(title = "模块", businessType = BusinessType.UPDATE)
    @PutMapping("edit")
    @ApiOperation(value = " 修改模块",notes = "修改模块")
    public R edit(@RequestBody BasMessage basMessage)
    {
    return toOk(basMessageService.updateBasMessage(basMessage));
    }

    /**
    * 删除模块
    */
    @PreAuthorize("@ss.hasPermi('BasMessage:BasMessage:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块",notes = "删除模块")
    public R remove(@RequestBody List<Long> ids)
    {
    return toOk(basMessageService.deleteBasMessageByIds(ids));
    }

}
