package com.szmsd.track.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.common.core.utils.poi.ExcelUtil;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.common.log.annotation.Log;
import com.szmsd.common.log.enums.BusinessType;
import com.szmsd.common.plugin.annotation.AutoValue;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.*;
import com.szmsd.track.event.ChangeDelOutboundLatestTrackEvent;
import com.szmsd.track.imported.DefaultAnalysisEventListener;
import com.szmsd.track.imported.ImportMessage;
import com.szmsd.track.imported.ImportResult;
import com.szmsd.track.service.ITrackService;
import com.szmsd.track.util.EasyExcelFactoryUtil;
import com.szmsd.track.util.SHA256Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */

@Slf4j
@Api(tags = {""})
@RestController
@RequestMapping("/del-track")
public class TrackController extends BaseController {

    @Resource
    private ITrackService delTrackService;

    @Value("${webhook.secret}")
    private String webhookSecret;

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 查询模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:list')")
    @GetMapping("/list")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public TableDataInfo list(Track delTrack) {
        startPage();
        List<Track> list = delTrackService.selectDelTrackList(delTrack);
        return getDataTable(list);
    }

    @PostMapping("/commonTrackList")
    @ApiOperation(value = "查询模块列表", notes = "查询模块列表")
    @AutoValue
    public R<TrackMainCommonDto> commonTrackList(@RequestBody List<String> orderNos) {

        R<TrackMainCommonDto> commonTrackListRs = delTrackService.commonTrackList(orderNos);

        return commonTrackListRs;
    }

    /**
     * 导出模块列表
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:export')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    @ApiOperation(value = "导出模块列表", notes = "导出模块列表")
    public void export(HttpServletResponse response, Track delTrack) throws IOException {
        List<Track> list = delTrackService.selectDelTrackList(delTrack);
        ExcelUtil<Track> util = new ExcelUtil<Track>(Track.class);
        util.exportExcel(response, list, "DelTrack");

    }

    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:importTrackExcelTemplate')")
    @GetMapping("/importTrackExcelTemplate")
    @ApiOperation(value = "下载导入模板", position = 100)
    public void collectionExportTemplate(HttpServletResponse response) {
        String filePath = "/template/DM_track.xlsx";
        String fileName = "轨迹导入";
        this.downloadTemplate(response, filePath, fileName, "xlsx");
    }

    @Log(title = "模块", businessType = BusinessType.IMPORT)
    @ApiOperation(value = "导入", notes = "导入")
    @PostMapping("/importTrack")
    public R<ImportResult> importTrack(MultipartFile file){
        AssertUtil.notNull(file, "上传文件不存在");
        try {
            byte[] byteArray = IOUtils.toByteArray(file.getInputStream());
            DefaultAnalysisEventListener<ImportTrackDto> defaultAnalysisEventListener = EasyExcelFactoryUtil.read(new ByteArrayInputStream(byteArray), ImportTrackDto.class, 0, 1);
            if (defaultAnalysisEventListener.isError()) {
                return R.ok(ImportResult.buildFail(defaultAnalysisEventListener.getMessageList()));
            }
            List<ImportTrackDto> dataList = defaultAnalysisEventListener.getList();
            if (CollectionUtils.isEmpty(dataList)) {
                return R.ok(ImportResult.buildFail(ImportMessage.build("导入数据不能为空")));
            }
            List<ImportMessage> messageList = new ArrayList<>();
            List<Track> tracks = BeanMapperUtil.mapList(dataList, Track.class);
            int i = 1;
            for(ImportTrackDto track : dataList){
                if (StringUtils.isBlank(track.getOrderNo())){
                    messageList.add(new ImportMessage(i, 1, null ,"订单号不能为空" ));
                }
                if (StringUtils.isBlank(track.getTrackingNo())){
                    messageList.add(new ImportMessage(i, 2, null ,"物流跟踪号不能为空" ));
                }
                if (StringUtils.isBlank(track.getTrackingStatus())){
                    messageList.add(new ImportMessage(i, 1, null ,"轨迹状态不能为空" ));
                }
                i++;

            }
            if (CollectionUtils.isNotEmpty(messageList)) {
                return R.ok(ImportResult.buildFail(messageList));
            }
            tracks.forEach(track -> {
                track.setSource("2");
                track.setTrackingTime(new Date());
                applicationContext.publishEvent(new ChangeDelOutboundLatestTrackEvent(track));
            });
            delTrackService.saveBatch(tracks);
            return R.ok();
        } catch (IOException e) {
            e.printStackTrace();
            return R.ok(ImportResult.buildFail(ImportMessage.build(e.getMessage())));
        }
    }

    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:addOrUpdate')")
    @Log(title = "模块", businessType = BusinessType.INSERT)
    @PostMapping("addOrUpdate")
    public R addOrUpdate(@RequestBody Track delTrack){

        delTrackService.saveOrUpdateTrack(delTrack);

        return R.ok();
    }

    /**
     * 删除模块
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:remove')")
    @Log(title = "模块", businessType = BusinessType.DELETE)
    @DeleteMapping("remove")
    @ApiOperation(value = "删除模块", notes = "删除模块")
    public R remove(@RequestBody List<String> ids) {
        return toOk(delTrackService.deleteDelTrackByIds(ids));
    }


    @ApiOperation(value = "用于接收TrackingYee回调的路由信息", notes = "")
    @PostMapping("/traceCallback")
    public R traceCallback(@RequestHeader(value = "trackingyee-webhook-signature") String trackingyeeSign,@RequestBody Object params){
        if(params == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 验证签名
//        String trackingyeeSign = request.getHeader("trackingyee-webhook-signature");
        String requestStr = JSONObject.toJSONString(params, SerializerFeature.WriteMapNullValue);
        String verifySign = SHA256Util.getSHA256Str(webhookSecret + requestStr);
        log.info("trackingyeeSign: {}", trackingyeeSign);
        log.info("待加密验签字符串: {}", webhookSecret + requestStr);
        log.info("verifySign: {}", verifySign);
        TrackingYeeTraceDto trackingYeeTraceDto = JSONObject.parseObject(requestStr, TrackingYeeTraceDto.class);
        if (StringUtils.isBlank(trackingyeeSign) || !trackingyeeSign.equalsIgnoreCase(verifySign)) {
            return R.failed("非法请求，验签失败！");
        }
        if (trackingYeeTraceDto == null) {
            return R.failed("非法请求，参数异常！");
        }
        // 处理数据
        delTrackService.traceCallback(trackingYeeTraceDto);
        return R.ok();
    }

    @ApiOperation(value = "获取轨迹分析", notes = "获取轨迹分析")
    @GetMapping("getTrackAnalysis")
    public R getTrackAnalysis(TrackAnalysisRequestDto requestDto, @RequestHeader("langr") String langr){
        requestDto.setLang(langr);
        return R.ok(delTrackService.getTrackAnalysis(requestDto));
    }

    @ApiOperation(value = "获取轨迹状态下的各个发货服务订单量分析", notes = "获取轨迹状态下的各个发货服务订单量分析")
    @GetMapping("getProductAnalysis")
    public R getProductAnalysis(TrackAnalysisRequestDto requestDto){
        return R.ok(delTrackService.getProductServiceAnalysis(requestDto));
    }

    /**
     * 导出轨迹分析
     */
    @PreAuthorize("@ss.hasPermi('DelTrack:DelTrack:exportTrackAnalysis')")
    @Log(title = "模块", businessType = BusinessType.EXPORT)
    @GetMapping("/exportTrackAnalysis")
    @ApiOperation(value = "导出轨迹分析", notes = "导出轨迹分析")
    public void exportTrackAnalysis(HttpServletResponse response, TrackAnalysisRequestDto requestDto, @RequestHeader("langr") String langr) throws IOException {
        requestDto.setLang(langr);
        List<TrackAnalysisExportDto> list = delTrackService.getAnalysisExportData(requestDto);
        ExcelUtil<TrackAnalysisExportDto> util = new ExcelUtil<TrackAnalysisExportDto>(TrackAnalysisExportDto.class);
        util.exportExcel(response, list, "TrackAnalysis");
    }

    /**
     * 下载模板
     *
     * @param response response
     * @param filePath 文件存放路径，${server.tomcat.basedir}配置的目录和resources目录下
     * @param fileName 文件名称
     * @param ext      扩展名
     */
    private void downloadTemplate(HttpServletResponse response, String filePath, String fileName, String ext) {
        // 先去模板目录中获取模板
        // 模板目录中没有模板再从项目中获取模板
        String basedir = SpringUtils.getProperty("server.tomcat.basedir", "/u01/www/ck1/delivery");
        File file = new File(basedir + "/" + filePath);
        InputStream inputStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                response.setHeader("File-Source", "local");
            } else {
                org.springframework.core.io.Resource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
                response.setHeader("File-Source", "resource");
            }
            outputStream = response.getOutputStream();
            //response为HttpServletResponse对象
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            //Loading plan.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
            String efn = URLEncoder.encode(fileName, "utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + efn + "." + ext);
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("400", "文件不存在，" + e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CommonException("500", "文件流处理失败，" + e.getMessage());
        } finally {
            IoUtil.flush(outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(inputStream);
        }
    }
}
