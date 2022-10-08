package com.szmsd.finance.controller;

import com.google.common.collect.Lists;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.BatchDownFilesUtils;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.finance.service.AccountBillRecordService;
import com.szmsd.finance.vo.BillBalanceVO;
import com.szmsd.finance.vo.BillGeneratorRequestVO;
import com.szmsd.finance.vo.EleBillQueryVO;
import com.szmsd.finance.vo.ElectronicBillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.util.List;

@Api(tags = {"流水账单统计"})
@RestController
@RequestMapping("/bill")
public class AccountSerialBillVitalController extends BaseController {

    @Resource
    private AccountBillRecordService accountSerialBillService;

    @Value("${filepath}")
    private String filePath;

    //@AutoValue
    //@PreAuthorize("@ss.hasPermi('AccountSerialBill:listPage')")
    @ApiOperation(value = "电子账单列表")
    @GetMapping("/page")
    public TableDataInfo<ElectronicBillVO> electronicPage(EleBillQueryVO queryVO) {
        startPage();
        return getDataTable(accountSerialBillService.electronicPage(queryVO));
    }

    //@PreAuthorize("@ss.hasPermi('PreRecharge:save')")
    @ApiOperation(value = "账单生成")
    @PostMapping("/generator")
    public R<Integer> generatorBill(HttpServletRequest request, @RequestBody @Valid BillGeneratorRequestVO billRequestVO){
        return accountSerialBillService.generatorBill(request,billRequestVO);
    }

    @ApiOperation(value = "资金结余列表")
    @GetMapping("/balance-page")
    public R<List<BillBalanceVO>> balancePage(@Valid EleBillQueryVO queryVO) {

        return R.ok(accountSerialBillService.balancePage(queryVO));
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

    @ApiOperation(value = "导出")
    @PostMapping ("/export")
    public void export(HttpServletResponse response, @RequestBody @Valid EleBillQueryVO requestVO) {

        accountSerialBillService.export(response,requestVO);
    }

}
